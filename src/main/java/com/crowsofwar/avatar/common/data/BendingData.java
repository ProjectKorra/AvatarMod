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

import java.util.HashSet;
import java.util.List;
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
	
	public BendingData() {
		bendings = new HashSet<>();
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
	
	List<BendingController> getBendingControllers();
	
	/**
	 * Get the BendingController with that type. Returns null if there is no
	 * bending controller for that type.
	 */
	BendingController getBendingController(BendingType type);
	
	/**
	 * Gets extra metadata for the given bending controller with that type, or
	 * null if there is no bending controller.
	 * <p>
	 * Will automatically create a state and sync changes if the controller is
	 * present.
	 */
	BendingState getBendingState(BendingType type);
	
	/**
	 * Get extra metadata for the given bending controller, returns null if no
	 * Bending controller.
	 * 
	 * @see #getBendingState(BendingType)
	 */
	BendingState getBendingState(BendingController controller);
	
	/**
	 * Returns whether a bending state for the bending controller is present.
	 * Does not add one if necessary.
	 */
	boolean hasBendingState(BendingController controller);
	
	List<BendingState> getAllBendingStates();
	
	void clearBendingStates();
	
	/**
	 * Adds the bending state to this player data, replacing the existing one of
	 * that type if necessary.
	 */
	void addBendingState(BendingState state);
	
	/**
	 * Removes that bending state from this player data. Note: Must be the exact
	 * instance already present to successfully occur.
	 */
	void removeBendingState(BendingState state);
	
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
