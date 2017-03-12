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

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbstractBendingData.DataCategory;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public interface BendingData {
	
	/**
	 * Check if the player has that bending controller
	 */
	boolean hasBending(BendingController bending);
	
	/**
	 * Check if the player has that type of bending
	 */
	boolean hasBending(BendingType type);
	
	/**
	 * If the bending controller is not already present, adds the bending
	 * controller.
	 * <p>
	 * Also adds the state if it isn't present.
	 */
	void addBending(BendingController bending);
	
	/**
	 * If the bending controller is not already present, adds the bending
	 * controller.
	 */
	void addBending(BendingType type);
	
	/**
	 * Remove the specified bending controller and its associated state. Please
	 * note, this will be saved, so is permanent (unless another bending
	 * controller is added).
	 */
	void removeBending(BendingController bending);
	
	/**
	 * Remove the bending controller and its state with that type.
	 * 
	 * @see #removeBending(BendingController)
	 */
	void removeBending(BendingType type);
	
	List<BendingController> getAllBending();
	
	boolean hasStatusControl(StatusControl control);
	
	void addStatusControl(StatusControl control);
	
	void removeStatusControl(StatusControl control);
	
	List<StatusControl> getAllStatusControls();
	
	void clearStatusControls();
	
	boolean hasAbilityData(BendingAbility ability);
	
	/**
	 * Retrieves data about the given ability. Will create data if necessary.
	 */
	AbilityData getAbilityData(BendingAbility ability);
	
	void setAbilityData(BendingAbility ability, AbilityData data);
	
	/**
	 * Gets a list of all ability data contained in this player data.
	 */
	List<AbilityData> getAllAbilityData();
	
	/**
	 * Removes all ability data associations
	 */
	void clearAbilityData();
	
	/**
	 * Gets the chi information about the bender
	 */
	Chi chi();
	
	void setChi(Chi chi);
	
	boolean isSkating();
	
	void setSkating(boolean skating);
	
	float getFallAbsorption();
	
	void setFallAbsorption(float fallAbsorption);
	
	int getTimeInAir();
	
	void setTimeInAir(int time);
	
	int getAbilityCooldown();
	
	void setAbilityCooldown(int cooldown);
	
	void decrementCooldown();
	
	boolean isWallJumping();
	
	void setWallJumping(boolean wallJumping);
	
	/**
	 * Save this BendingData
	 */
	void save(DataCategory category, DataCategory... addditionalCategories);
	
}