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
	private final Map<BendingController, BendingState> bendingStates;
	
	public BendingData() {
		bendings = new HashSet<>();
		bendingStates = new HashMap<>();
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
			save();
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
			save();
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
	// BENDING STATES
	// ================================================================================
	
	public boolean hasBendingState(BendingController controller) {
		return bendingStates.get(controller) != null;
	}
	
	public boolean hasBendingState(BendingType type) {
		return hasBendingState(BendingManager.getBending(type));
	}
	
	/**
	 * Get extra metadata for the given bending controller, returns null if no
	 * Bending controller.
	 * 
	 * @see #getBendingState(BendingType)
	 */
	public BendingState getBendingState(BendingController controller) {
		if (!hasBending(controller)) {
			return null;
		}
		
		BendingState state = bendingStates.get(controller);
		if (state == null) {
			state = controller.createState(this);
			addBendingState(state);
		}
		
		return state;
	}
	
	/**
	 * Gets extra metadata for the given bending controller with that type, or
	 * null if there is no bending controller.
	 * <p>
	 * Will automatically create a state and sync changes if the controller is
	 * present.
	 */
	public BendingState getBendingState(BendingType type) {
		return getBendingState(BendingManager.getBending(type));
	}
	
	/**
	 * Adds the bending state to this player data, not replacing the existing
	 * one of that type if necessary.
	 */
	public void addBendingState(BendingState state) {
		BendingType type = state.getType();
		if (hasBending(type) && !hasBendingState(type)) {
			bendingStates.put(BendingManager.getBending(type), state);
			save();
		}
	}
	
	/**
	 * Removes that bending state from this player data. Note: Must be the exact
	 * instance already present to successfully occur.
	 */
	public void removeBendingState(BendingState state) {
		BendingType type = state.getType();
		if (hasBendingState(type)) {
			bendingStates.remove(type);
			save();
		}
	}
	
	public List<BendingState> getAllBendingStates() {
		return new ArrayList<>(bendingStates.values());
	}
	
	Set<StatusControl> getActiveStatusControls();
	
	boolean hasStatusControl(StatusControl status);
	
	void addStatusControl(StatusControl control);
	
	void removeStatusControl(StatusControl control);
	
	void clearStatusControls();
	
	/**
	 * Retrieves data about the given ability. Will create data if necessary.
	 */
	AbilityData getAbilityData(BendingAbility ability);
	
	/**
	 * Gets a list of all ability data contained in this player data. The list
	 * is immutable.
	 */
	List<AbilityData> getAllAbilityData();
	
	void clearAbilityData();
	
	/**
	 * Gets the chi information about the bender
	 */
	Chi chi();
	
	void setChi(Chi chi);
	
	void sendBendingState(BendingState state);
	
	default void sync() {}
	
	boolean isSkating();
	
	void setSkating(boolean skating);
	
	float getFallAbsorption();
	
	void setFallAbsorption(float amount);
	
	/**
	 * Save this BendingData
	 */
	protected abstract void save();
	
}
