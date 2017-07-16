/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.data.*;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import java.util.*;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;

public class AvatarPlayerData extends PlayerData {
	
	private static PlayerDataFetcher<AvatarPlayerData> fetcher;

	private final AbstractBendingData bendingData;

	/**
	 * Changed DataCategories since last sent a packet
	 */
	private SortedSet<DataCategory> changed;
	
	public AvatarPlayerData(DataSaver dataSaver, UUID playerID, EntityPlayer player) {
		super(dataSaver, playerID, player);
		
		boolean isClient = !(player instanceof EntityPlayerMP);
		
		bendingData = new AbstractBendingData() {
			@Override
			public void save(DataCategory category) {
				AvatarPlayerData.this.save(category);
			}
		};
		
		changed = new TreeSet<>();
		
	}
	
	@Override
	protected void readPlayerDataFromNBT(NBTTagCompound readFrom) {
		
		List<BendingStyle> bendings = new ArrayList<>();
		AvatarUtils.readList(bendings,
				compound -> BendingStyles.get(compound.getUniqueId("ControllerID")), readFrom,
				"BendingControllers");
		clearBending();
		for (BendingStyle bending : bendings) {
			addBending(bending);
		}
		
		List<StatusControl> scs = new ArrayList<>();
		AvatarUtils.readList(scs, nbtTag -> StatusControl.lookup(nbtTag.getInteger("Id")), readFrom,
				"StatusControls");
		clearStatusControls();
		for (StatusControl sc : scs) {
			addStatusControl(sc);
		}
		
		Map<Ability, AbilityData> abilityData = new HashMap<>();
		AvatarUtils.readMap(abilityData, nbt -> Abilities.get(nbt.getUniqueId("Id")), nbt -> {
			Ability ability = Abilities.get(nbt.getUniqueId("AbilityId"));
			AbilityData data = new AbilityData(this, ability);
			data.readFromNbt(nbt);
			return data;
		}, readFrom, "AbilityData");
		clearAbilityData();
		for (Map.Entry<Ability, AbilityData> entry : abilityData.entrySet()) {
			setAbilityData(entry.getKey().getId(), entry.getValue());
		}
		
		getMiscData().readFromNbt(nestedCompound(readFrom, "Misc"));
		
		chi().readFromNBT(readFrom);
		
		List<TickHandler> tickHandlers = new ArrayList<>();
		AvatarUtils.readList(tickHandlers, //
				nbt -> TickHandler.fromId(nbt.getInteger("Id")), //
				readFrom, "TickHandlers");
		clearTickHandlers();
		for (TickHandler handler : tickHandlers) {
			addTickHandler(handler);
		}
		
	}
	
	@Override
	protected void writePlayerDataToNBT(NBTTagCompound writeTo) {
		
		AvatarUtils.writeList(getAllBending(),
				(compound, controller) -> compound.setUniqueId("ControllerID", controller.getId()), writeTo,
				"BendingControllers");
		
		AvatarUtils.writeList(getAllStatusControls(),
				(nbtTag, control) -> nbtTag.setInteger("Id", control.id()), writeTo, "StatusControls");
		
		AvatarUtils.writeMap(getAbilityDataMap(), //
				(nbt, abilityId) -> {
					nbt.setUniqueId("Id", abilityId);
					nbt.setString("_AbilityName", Abilities.getName(abilityId) + "");
				}, (nbt, data) -> {
					nbt.setUniqueId("AbilityId", data.getAbility().getId());
					data.writeToNbt(nbt);
				}, writeTo, "AbilityData");
		
		getMiscData().writeToNbt(nestedCompound(writeTo, "Misc"));
		
		chi().writeToNBT(writeTo);
		
		AvatarUtils.writeList(getAllTickHandlers(), //
				(nbt, handler) -> nbt.setInteger("Id", handler.id()), //
				writeTo, "TickHandlers");
		
	}
	
	public void save(DataCategory category) {
		changed.add(category);
		sendPacket();
		saveChanges();
	}
	
	public void saveAll() {
		changed.addAll(Arrays.asList(DataCategory.values()));
		sendPacket();
		saveChanges();
	}
	
	private void sendPacket() {
		
		PacketCPlayerData packet = new PacketCPlayerData(bendingData, playerID, changed);
		EntityPlayer player = this.getPlayerEntity();
		if (player != null && !player.world.isRemote) {
			
			// Look at who is tracking this player, to avoid unnecessarily
			// sending packets to extra players
			EntityTracker tracker = ((WorldServer) player.world).getEntityTracker();
			
			List<EntityPlayer> nearbyPlayers = new ArrayList<>();
			nearbyPlayers.add(player);
			nearbyPlayers.addAll(tracker.getTrackingPlayers(player));
			
			// Find the correct range to send the packet to
			double rangeSq = 0;
			for (EntityPlayer p : nearbyPlayers) {
				if (p.getDistanceSqToEntity(player) > rangeSq) {
					rangeSq = p.getDistanceSqToEntity(player);
				}
			}
			double range = Math.sqrt(rangeSq) + 0.01;// +0.01 "just in case"
			
			AvatarMod.network.sendToAllAround(packet,
					new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, range));
			
		}
		
	}

	public BendingData getData() {
		return bendingData;
	}

	public static void initFetcher(PlayerDataFetcher<AvatarPlayerData> clientFetcher) {
		fetcher = new PlayerDataFetcherSided<>(clientFetcher,
				new PlayerDataFetcherServer<>(AvatarWorldData::getDataFromWorld));
	}
	
	public static PlayerDataFetcher<AvatarPlayerData> fetcher() {
		return fetcher;
	}

}
