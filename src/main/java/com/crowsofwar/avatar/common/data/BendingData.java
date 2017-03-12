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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class BendingData {
	
	private final Set<BendingController> bendings;
	private final Set<StatusControl> statusControls;
	private final Map<BendingAbility, AbilityData> abilityData;
	private Chi chi;
	
	public BendingData() {
		bendings = new HashSet<>();
		statusControls = new HashSet<>();
		abilityData = new HashMap<>();
		chi = new Chi(this);
	}
	
	// ================================================================================
	// BENDINGS METHODS
	// ================================================================================
	
	/**
	 * Check if the player has that bending controller
	 */
	public boolean hasBending(BendingController bending) {
		return bendings.contains(bending);
	}
	
	/**
	 * Check if the player has that type of bending
	 */
	public boolean hasBending(BendingType type) {
		return hasBending(BendingManager.getBending(type));
	}
	
	/**
	 * If the bending controller is not already present, adds the bending
	 * controller.
	 * <p>
	 * Also adds the state if it isn't present.
	 */
	public void addBending(BendingController bending) {
		if (bendings.add(bending)) {
			save(DataCategory.BENDING);
		}
	}
	
	/**
	 * If the bending controller is not already present, adds the bending
	 * controller.
	 */
	public void addBending(BendingType type) {
		addBending(BendingManager.getBending(type));
	}
	
	/**
	 * Remove the specified bending controller and its associated state. Please
	 * note, this will be saved, so is permanent (unless another bending
	 * controller is added).
	 */
	public void removeBending(BendingController bending) {
		if (bendings.remove(bending)) {
			save(DataCategory.BENDING);
		}
	}
	
	/**
	 * Remove the bending controller and its state with that type.
	 * 
	 * @see #removeBending(BendingController)
	 */
	public void removeBending(BendingType type) {
		removeBending(BendingManager.getBending(type));
	}
	
	public List<BendingController> getAllBending() {
		return new ArrayList<>(bendings);
	}
	
	// ================================================================================
	// STATUS CONTROLS
	// ================================================================================
	
	public boolean hasStatusControl(StatusControl control) {
		return statusControls.contains(control);
	}
	
	public void addStatusControl(StatusControl control) {
		if (statusControls.add(control)) {
			save(DataCategory.STATUS_CONTROLS);
		}
	}
	
	public void removeStatusControl(StatusControl control) {
		if (statusControls.remove(control)) {
			save(DataCategory.STATUS_CONTROLS);
		}
	}
	
	public List<StatusControl> getAllStatusControls() {
		return new ArrayList<>(statusControls);
	}
	
	public void clearStatusControls() {
		statusControls.clear();
	}
	
	// ================================================================================
	// ABILITY DATA
	// ================================================================================
	
	public boolean hasAbilityData(BendingAbility ability) {
		return abilityData.get(ability) != null;
	}
	
	/**
	 * Retrieves data about the given ability. Will create data if necessary.
	 */
	public AbilityData getAbilityData(BendingAbility ability) {
		AbilityData data = abilityData.get(ability);
		if (data == null) {
			data = new AbilityData(this, ability);
			abilityData.put(ability, data);
			save(DataCategory.BENDING);
		}
		
		return data;
	}
	
	public void setAbilityData(BendingAbility ability, AbilityData data) {
		abilityData.put(ability, data);
	}
	
	/**
	 * Gets a list of all ability data contained in this player data.
	 */
	public List<AbilityData> getAllAbilityData() {
		return new ArrayList<>(abilityData.values());
	}
	
	/**
	 * Removes all ability data associations
	 */
	public void clearAbilityData() {
		abilityData.clear();
	}
	
	// ================================================================================
	// CHI
	// ================================================================================
	
	/**
	 * Gets the chi information about the bender
	 */
	public Chi chi() {
		return chi;
	}
	
	public void setChi(Chi chi) {
		this.chi = chi;
		save(DataCategory.CHI);
	}
	
	void sendBendingState(BendingState state);
	
	boolean isSkating();
	
	void setSkating(boolean skating);
	
	float getFallAbsorption();
	
	void setFallAbsorption(float amount);
	
	/**
	 * Save this BendingData
	 */
	public abstract void save(DataCategory category, DataCategory... addditionalCategories);
	
	public enum DataCategory {
		BENDING,
		STATUS_CONTROLS,
		ABILITY_DATA,
		CHI
	}
	
}
