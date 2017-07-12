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

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.data.DataSaver;
import com.crowsofwar.gorecore.data.PlayerData;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;
import com.crowsofwar.gorecore.data.PlayerDataFetcherServer;
import com.crowsofwar.gorecore.data.PlayerDataFetcherSided;

import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class AvatarPlayerData extends PlayerData implements BendingData {
	
	private static PlayerDataFetcher<AvatarPlayerData> fetcher;
	
	private final AbstractBendingData bendingData;
	
	@Override
	public BendingController getActiveBending() {
		return bendingData.getActiveBending();
	}
	
	@Override
	public BendingType getActiveBendingType() {
		return bendingData.getActiveBendingType();
	}
	
	@Override
	public void setActiveBending(BendingController controller) {
		bendingData.setActiveBending(controller);
	}
	
	@Override
	public void setActiveBendingType(BendingType type) {
		bendingData.setActiveBendingType(type);
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
		
		List<BendingController> bendings = new ArrayList<>();
		AvatarUtils.readList(bendings,
				compound -> BendingController.find(compound.getInteger("ControllerID")), readFrom,
				"BendingControllers");
		clearBending();
		for (BendingController bending : bendings) {
			addBending(bending);
		}
		
		List<StatusControl> scs = new ArrayList<>();
		AvatarUtils.readList(scs, nbtTag -> StatusControl.lookup(nbtTag.getInteger("Id")), readFrom,
				"StatusControls");
		clearStatusControls();
		for (StatusControl sc : scs) {
			addStatusControl(sc);
		}
		
		Map<BendingAbility, AbilityData> abilityData = new HashMap<>();
		AvatarUtils.readMap(abilityData, nbt -> BendingManager.getAbility(nbt.getInteger("Id")), nbt -> {
			BendingAbility ability = BendingManager.getAbility(nbt.getInteger("AbilityId"));
			AbilityData data = new AbilityData(this, ability);
			data.readFromNbt(nbt);
			return data;
		}, readFrom, "AbilityData");
		clearAbilityData();
		for (Map.Entry<BendingAbility, AbilityData> entry : abilityData.entrySet()) {
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
				(compound, controller) -> compound.setInteger("ControllerID", controller.getID()), writeTo,
				"BendingControllers");
		
		AvatarUtils.writeList(getAllStatusControls(),
				(nbtTag, control) -> nbtTag.setInteger("Id", control.id()), writeTo, "StatusControls");
		
		AvatarUtils.writeMap(getAbilityDataMap(), //
				(nbt, ability) -> {
					nbt.setInteger("Id", ability.getId());
					nbt.setString("_AbilityName", ability.getName());
				}, (nbt, data) -> {
					nbt.setInteger("AbilityId", data.getAbility().getId());
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
		if (player != null && !player.worldObj.isRemote) {
			
			// Look at who is tracking this player, to avoid unnecessarily
			// sending packets to extra players
			EntityTracker tracker = ((WorldServer) player.worldObj).getEntityTracker();
			
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
	
	@Override
	protected void saveChanges() {
		super.saveChanges();
		updateMaxChi();
	}
	
	private void updateMaxChi() {
		int chi = 0;
		chi += getAllBending().size() * 15;
		for (AbilityData aData : getAllAbilityData()) {
			if (!aData.isLocked() && hasBending(aData.getAbility().getBendingType())) {
				chi += 3;
				chi += aData.getLevel();
			}
		}
		if (chi >= 50) chi = 50;
		
		// needed to avoid StackOverflowError
		if (chi != chi().getMaxChi()) {
			float old = chi().getMaxChi();
			chi().setMaxChi(chi);
			
			// Don't need to wait for new chi to regen
			if (chi > old) {
				chi().changeTotalChi(chi - old);
			}
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
	public boolean hasBending(BendingController bending) {
		return bendingData.hasBending(bending);
	}
	
	@Override
	public boolean hasBending(BendingType type) {
		return bendingData.hasBending(type);
	}
	
	@Override
	public void addBending(BendingController bending) {
		bendingData.addBending(bending);
	}
	
	@Override
	public void addBending(BendingType type) {
		bendingData.addBending(type);
	}
	
	@Override
	public void removeBending(BendingController bending) {
		bendingData.removeBending(bending);
	}
	
	@Override
	public void removeBending(BendingType type) {
		bendingData.removeBending(type);
	}
	
	@Override
	public List<BendingController> getAllBending() {
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
	public boolean hasAbilityData(BendingAbility ability) {
		return bendingData.hasAbilityData(ability);
	}
	
	@Override
	public AbilityData getAbilityData(BendingAbility ability) {
		return bendingData.getAbilityData(ability);
	}
	
	@Override
	public void setAbilityData(BendingAbility ability, AbilityData data) {
		bendingData.setAbilityData(ability, data);
	}
	
	@Override
	public List<AbilityData> getAllAbilityData() {
		return bendingData.getAllAbilityData();
	}
	
	@Override
	public Map<BendingAbility, AbilityData> getAbilityDataMap() {
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
	public void setAllBending(List<BendingController> bending) {
		bendingData.setAllBending(bending);
	}
	
	@Override
	public void setAllStatusControls(List<StatusControl> controls) {
		bendingData.setAllStatusControls(controls);
	}
	
	@Override
	public void setAbilityDataMap(Map<BendingAbility, AbilityData> map) {
		bendingData.setAbilityDataMap(map);
	}
	
	@Override
	public void setAllTickHandlers(List<TickHandler> handlers) {
		bendingData.setAllTickHandlers(handlers);
	}
	
}
