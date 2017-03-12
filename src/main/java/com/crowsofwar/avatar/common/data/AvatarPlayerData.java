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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.network.DataTransmitter;
import com.crowsofwar.avatar.common.network.Networker;
import com.crowsofwar.avatar.common.network.Networker.Property;
import com.crowsofwar.avatar.common.network.PlayerDataContext;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.data.DataSaver;
import com.crowsofwar.gorecore.data.PlayerData;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;
import com.crowsofwar.gorecore.data.PlayerDataFetcherServer;
import com.crowsofwar.gorecore.data.PlayerDataFetcherSided;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class AvatarPlayerData extends PlayerData implements BendingData {
	
	// TODO change player data lists into sets, when applicable
	
	public static final Networker.Property<List<BendingController>> KEY_CONTROLLERS = new Property<>(1);
	public static final Networker.Property<Map<BendingAbility, AbilityData>> KEY_ABILITY_DATA = new Property<>(
			2);
	public static final Networker.Property<Set<StatusControl>> KEY_STATUS_CONTROLS = new Property<>(3);
	public static final Networker.Property<Boolean> KEY_SKATING = new Property<>(4);
	public static final Networker.Property<Chi> KEY_CHI = new Property<>(5);
	
	private static PlayerDataFetcher<AvatarPlayerData> fetcher;
	
	private final Networker networker;
	private final AbstractBendingData bendingData;
	
	public AvatarPlayerData(DataSaver dataSaver, UUID playerID, EntityPlayer player) {
		super(dataSaver, playerID, player);
		
		boolean isClient = player instanceof AbstractClientPlayer;
		networker = new Networker(!isClient, PacketCPlayerData.class,
				net -> new PacketCPlayerData(net, playerID));
		
		bendingData = new AbstractBendingData() {
			
			@Override
			public void save(DataCategory category, DataCategory... addditionalCategories) {
				AvatarPlayerData.this.save(category, addditionalCategories);
			}
		};
		
		for (DataCategory category : DataCategory.values()) {
			
			networker.register(//
					category.get(this), //
					(DataTransmitter<Object, PlayerDataContext>) category.getTransmitter(), //
					(Networker.Property<Object>) category.property());
			
		}
		
	}
	
	@Override
	protected void readPlayerDataFromNBT(NBTTagCompound readFrom) {
		
		AvatarPlayerData playerData = this;
		AvatarUtils.readList(bendingControllerList,
				compound -> BendingController.find(compound.getInteger("ControllerID")), readFrom,
				"BendingControllers");
		
		bendingControllers.clear();
		for (BendingController controller : bendingControllerList) {
			bendingControllers.put(controller.getType(), controller);
		}
		
		AvatarUtils.readList(statusControls, nbtTag -> StatusControl.lookup(nbtTag.getInteger("Id")),
				readFrom, "StatusControls");
		
		AvatarUtils.readMap(abilityData, nbt -> BendingManager.getAbility(nbt.getInteger("Id")), nbt -> {
			BendingAbility ability = BendingManager.getAbility(nbt.getInteger("AbilityId"));
			AbilityData data = new AbilityData(this, ability);
			data.readFromNbt(nbt);
			return data;
		}, readFrom, "AbilityData");
		
		wallJumping = readFrom.getBoolean("WallJumping");
		fallAbsorption = readFrom.getFloat("FallAbsorption");
		timeInAir = readFrom.getInteger("TimeInAir");
		skating = readFrom.getBoolean("WaterSkating");
		abilityCooldown = readFrom.getInteger("AbilityCooldown");
		
		chi.readFromNBT(readFrom);
		
	}
	
	@Override
	protected void writePlayerDataToNBT(NBTTagCompound writeTo) {
		
		AvatarUtils.writeList(bendingControllerList,
				(compound, controller) -> compound.setInteger("ControllerID", controller.getID()), writeTo,
				"BendingControllers");
		AvatarUtils.writeList(statusControls, (nbtTag, control) -> nbtTag.setInteger("Id", control.id()),
				writeTo, "StatusControls");
		
		AvatarUtils.writeMap(abilityData, //
				(nbt, ability) -> {
					nbt.setInteger("Id", ability.getId());
					nbt.setString("_AbilityName", ability.getName());
				}, (nbt, data) -> {
					nbt.setInteger("AbilityId", data.getAbility().getId());
					data.writeToNbt(nbt);
				}, writeTo, "AbilityData");
		
		writeTo.setBoolean("WallJumping", wallJumping);
		writeTo.setFloat("FallAbsorption", fallAbsorption);
		writeTo.setInteger("TimeInAir", timeInAir);
		writeTo.setBoolean("WaterSkating", skating);
		writeTo.setInteger("AbilityCooldown", abilityCooldown);
		
		chi().writeToNBT(writeTo);
		
	}
	
	@Override
	public void save(DataCategory category, DataCategory... additionalCategories) {
		
		networker.markChanged((Networker.Property<Object>) category.property(), category.get(this));
		for (DataCategory cat : additionalCategories) {
			networker.markChanged((Networker.Property<Object>) cat.property(), cat.get(this));
		}
		
		networker.sendUpdated();
		saveChanges();
		
	}
	
	public Networker getNetworker() {
		return networker;
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
	public boolean isSkating() {
		return bendingData.isSkating();
	}
	
	@Override
	public void setSkating(boolean skating) {
		bendingData.setSkating(skating);
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
	
}
