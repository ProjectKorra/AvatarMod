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
import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.StatusControl;
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

public class AvatarPlayerData extends PlayerData implements BendingData {
	
	private static PlayerDataFetcher<AvatarPlayerData> fetcher;
	
	private final AbstractBendingData bendingData;
	
	@Override
	public BendingStyle getActiveBending() {
		return bendingData.getActiveBending();
	}
	
	@Override
	public int getActiveBendingId() {
		return bendingData.getActiveBendingId();
	}
	
	@Override
	public void setActiveBending(BendingStyle controller) {
		bendingData.setActiveBending(controller);
	}
	
	@Override
	public void setActiveint(int type) {
		bendingData.setActiveint(type);
	}
	
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
		
		AvatarPlayerData playerData = this;
		
		List<BendingStyle> bendings = new ArrayList<>();
		AvatarUtils.readList(bendings,
				compound -> BendingStyle.find(compound.getInteger("ControllerID")), readFrom,
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
			setAbilityData(entry.getKey(), entry.getValue());
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
				(compound, controller) -> compound.setInteger("ControllerID", controller.getId()), writeTo,
				"BendingControllers");
		
		AvatarUtils.writeList(getAllStatusControls(),
				(nbtTag, control) -> nbtTag.setInteger("Id", control.id()), writeTo, "StatusControls");
		
		AvatarUtils.writeMap(getAbilityDataMap(), //
				(nbt, ability) -> {
					nbt.setUniqueId("Id", ability.getId());
					nbt.setString("_AbilityName", ability.getName());
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
	
	@Override
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
		
		PacketCPlayerData packet = new PacketCPlayerData(this, playerID, changed);
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
	
	public static void initFetcher(PlayerDataFetcher<AvatarPlayerData> clientFetcher) {
		fetcher = new PlayerDataFetcherSided<>(clientFetcher,
				new PlayerDataFetcherServer<>(AvatarWorldData::getDataFromWorld));
	}
	
	public static PlayerDataFetcher<AvatarPlayerData> fetcher() {
		return fetcher;
	}
	
	// ================================================================================
	// DELEGATES
	// ================================================================================
	
	@Override
	public boolean hasBending(BendingStyle bending) {
		return bendingData.hasBending(bending);
	}
	
	@Override
	public boolean hasBending(int type) {
		return bendingData.hasBending(type);
	}
	
	@Override
	public void addBending(BendingStyle bending) {
		bendingData.addBending(bending);
	}
	
	@Override
	public void addBending(int type) {
		bendingData.addBending(type);
	}
	
	@Override
	public void removeBending(BendingStyle bending) {
		bendingData.removeBending(bending);
	}
	
	@Override
	public void removeBending(int type) {
		bendingData.removeBending(type);
	}
	
	@Override
	public List<BendingStyle> getAllBending() {
		return bendingData.getAllBending();
	}
	
	@Override
	public void clearBending() {
		bendingData.clearBending();
	}
	
	@Override
	public boolean hasStatusControl(StatusControl control) {
		return bendingData.hasStatusControl(control);
	}
	
	@Override
	public void addStatusControl(StatusControl control) {
		bendingData.addStatusControl(control);
	}
	
	@Override
	public void removeStatusControl(StatusControl control) {
		bendingData.removeStatusControl(control);
	}
	
	@Override
	public List<StatusControl> getAllStatusControls() {
		return bendingData.getAllStatusControls();
	}
	
	@Override
	public void clearStatusControls() {
		bendingData.clearStatusControls();
	}
	
	@Override
	public boolean hasAbilityData(Ability ability) {
		return bendingData.hasAbilityData(ability);
	}
	
	@Override
	public AbilityData getAbilityData(Ability ability) {
		return bendingData.getAbilityData(ability);
	}
	
	@Override
	public void setAbilityData(Ability ability, AbilityData data) {
		bendingData.setAbilityData(ability, data);
	}
	
	@Override
	public List<AbilityData> getAllAbilityData() {
		return bendingData.getAllAbilityData();
	}
	
	@Override
	public Map<Ability, AbilityData> getAbilityDataMap() {
		return bendingData.getAbilityDataMap();
	}
	
	@Override
	public void clearAbilityData() {
		bendingData.clearAbilityData();
	}
	
	@Override
	public Chi chi() {
		return bendingData.chi();
	}
	
	@Override
	public void setChi(Chi chi) {
		bendingData.setChi(chi);
	}
	
	@Override
	public boolean hasTickHandler(TickHandler handler) {
		return bendingData.hasTickHandler(handler);
	}
	
	@Override
	public void addTickHandler(TickHandler handler) {
		bendingData.addTickHandler(handler);
	}
	
	@Override
	public void removeTickHandler(TickHandler handler) {
		bendingData.removeTickHandler(handler);
	}
	
	@Override
	public List<TickHandler> getAllTickHandlers() {
		return bendingData.getAllTickHandlers();
	}
	
	@Override
	public void clearTickHandlers() {
		bendingData.clearTickHandlers();
	}
	
	@Override
	public MiscData getMiscData() {
		return bendingData.getMiscData();
	}
	
	@Override
	public void setMiscData(MiscData miscData) {
		bendingData.setMiscData(miscData);
	}
	
	@Override
	public float getFallAbsorption() {
		return bendingData.getFallAbsorption();
	}
	
	@Override
	public void setFallAbsorption(float fallAbsorption) {
		bendingData.setFallAbsorption(fallAbsorption);
	}
	
	@Override
	public int getTimeInAir() {
		return bendingData.getTimeInAir();
	}
	
	@Override
	public void setTimeInAir(int time) {
		bendingData.setTimeInAir(time);
	}
	
	@Override
	public int getAbilityCooldown() {
		return bendingData.getAbilityCooldown();
	}
	
	@Override
	public void setAbilityCooldown(int cooldown) {
		bendingData.setAbilityCooldown(cooldown);
	}
	
	@Override
	public void decrementCooldown() {
		bendingData.decrementCooldown();
	}
	
	@Override
	public boolean isWallJumping() {
		return bendingData.isWallJumping();
	}
	
	@Override
	public void setWallJumping(boolean wallJumping) {
		bendingData.setWallJumping(wallJumping);
	}
	
	@Override
	public int getPetSummonCooldown() {
		return bendingData.getPetSummonCooldown();
	}
	
	@Override
	public void setPetSummonCooldown(int cooldown) {
		bendingData.setPetSummonCooldown(cooldown);
	}
	
	@Override
	public void setAllBending(List<BendingStyle> bending) {
		bendingData.setAllBending(bending);
	}
	
	@Override
	public void setAllStatusControls(List<StatusControl> controls) {
		bendingData.setAllStatusControls(controls);
	}
	
	@Override
	public void setAbilityDataMap(Map<Ability, AbilityData> map) {
		bendingData.setAbilityDataMap(map);
	}
	
	@Override
	public void setAllTickHandlers(List<TickHandler> handlers) {
		bendingData.setAllTickHandlers(handlers);
	}
	
}
