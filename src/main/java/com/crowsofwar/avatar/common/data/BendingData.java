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

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import com.sun.jna.platform.win32.Advapi32Util;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenWaterlily;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public interface BendingData {

	// static methods
	public static BendingData get(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			return AvatarPlayerData.fetcher().fetch((EntityPlayer) entity).getData();
		} else {
			return Bender.create(entity).getData();
		}
	}

	public static BendingData get(World world, UUID playerId) {
		AvatarWorldData worldData = AvatarWorldData.getDataFromWorld(world);
		return worldData.getPlayerData(playerId).getData();
	}

	public static BendingData get(World world, String playerName) {
		AccountUUIDs.AccountId id = AccountUUIDs.getId(playerName);
		return get(world, id.getUUID());
	}

	// ================================================================================
	// BENDING CONTROLLERS
	// ================================================================================
	
	/**
	 * Check if the player has that bending controller
	 */
	boolean hasBending(BendingStyle bending);
	
	/**
	 * Check if the player has that type of bending
	 */
	boolean hasBending(UUID id);
	
	/**
	 * If the bending controller is not already present, adds the bending
	 * controller.
	 * <p>
	 * Also adds the state if it isn't present.
	 */
	void addBending(BendingStyle bending);
	
	/**
	 * If the bending controller is not already present, adds the bending
	 * controller.
	 */
	void addBending(UUID id);
	
	/**
	 * Remove the specified bending controller and its associated state. Please
	 * note, this will be saved, so is permanent (unless another bending
	 * controller is added).
	 */
	void removeBending(BendingStyle bending);
	
	/**
	 * Remove the bending controller and its state with that type.
	 * 
	 * @see #removeBending(BendingStyle)
	 */
	void removeBending(UUID id);
	
	List<BendingStyle> getAllBending();
	
	void setAllBending(List<BendingStyle> controller);
	
	void clearBending();
	
	// ================================================================================
	// ACTIVE BENDING
	// ================================================================================
	
	/**
	 * Gets the currently in-use bending controller. Null if player has no
	 * bending
	 */
	BendingStyle getActiveBending();
	
	/**
	 * Gets the type of the in-use bending controller. Null if the player has no
	 * bending
	 */
	UUID getActiveBendingId();
	
	/**
	 * Set the currently in-use bending. If null, will be rejected
	 */
	void setActiveBending(BendingStyle controller);
	
	/**
	 * Set the currently in-use type. If null, will be rejected
	 */
	void setActiveBending(UUID type);
	
	// ================================================================================
	// STATUS CONTROLS
	// ================================================================================
	
	boolean hasStatusControl(StatusControl control);
	
	void addStatusControl(StatusControl control);
	
	void removeStatusControl(StatusControl control);
	
	List<StatusControl> getAllStatusControls();
	
	void setAllStatusControls(List<StatusControl> controls);
	
	void clearStatusControls();
	
	// ================================================================================
	// ABILITY DATA
	// ================================================================================
	
	boolean hasAbilityData(UUID abilityId);
	
	/**
	 * Retrieves data about the given ability. Will create data if necessary.
	 */
	AbilityData getAbilityData(UUID abilityId);

	/**
	 * Retrieves data about the given ability. Will create data if necessary.
	 */
	default AbilityData getAbilityData(Ability ability) {
		return getAbilityData(ability.getId());
	}

	void setAbilityData(UUID abilityId, AbilityData data);
	
	/**
	 * Gets a list of all ability data contained in this player data.
	 */
	List<AbilityData> getAllAbilityData();
	
	Map<UUID, AbilityData> getAbilityDataMap();
	
	void setAbilityDataMap(Map<UUID, AbilityData> map);
	
	/**
	 * Removes all ability data associations
	 */
	void clearAbilityData();
	
	// ================================================================================
	// CHI
	// ================================================================================
	
	/**
	 * Gets the chi information about the bender
	 */
	Chi chi();
	
	void setChi(Chi chi);
	
	// ================================================================================
	// TICK HANDLERS
	// ================================================================================
	
	boolean hasTickHandler(TickHandler handler);
	
	void addTickHandler(TickHandler handler);
	
	void removeTickHandler(TickHandler handler);
	
	List<TickHandler> getAllTickHandlers();
	
	void setAllTickHandlers(List<TickHandler> handlers);
	
	void clearTickHandlers();
	
	// ================================================================================
	// MISC
	// ================================================================================
	
	MiscData getMiscData();
	
	void setMiscData(MiscData miscData);
	
	float getFallAbsorption();
	
	void setFallAbsorption(float fallAbsorption);
	
	int getTimeInAir();
	
	void setTimeInAir(int time);
	
	int getAbilityCooldown();
	
	void setAbilityCooldown(int cooldown);
	
	void decrementCooldown();
	
	boolean isWallJumping();
	
	void setWallJumping(boolean wallJumping);
	
	int getPetSummonCooldown();
	
	void setPetSummonCooldown(int cooldown);
	
	/**
	 * Save this BendingData
	 */
	void save(DataCategory category);

	default void saveAll() {
		for (DataCategory category : DataCategory.values()) {
			save(category);
		}
	}

}