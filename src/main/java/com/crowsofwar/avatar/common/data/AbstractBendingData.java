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
import com.crowsofwar.avatar.common.bending.StatusControl;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class AbstractBendingData implements BendingData {
	
	private final Set<BendingController> bendings;
	private final Set<StatusControl> statusControls;
	private final Map<BendingAbility, AbilityData> abilityData;
	private final Set<TickHandler> tickHandlers;
	private BendingController activeBending;
	private Chi chi;
	private MiscData miscData;
	
	public AbstractBendingData() {
		bendings = new HashSet<>();
		statusControls = new HashSet<>();
		abilityData = new HashMap<>();
		tickHandlers = new HashSet<>();
		activeBending = null;
		chi = new Chi(this);
		miscData = new MiscData(() -> save(DataCategory.MISC_DATA));
	}
	
	// ================================================================================
	// BENDINGS METHODS
	// ================================================================================
	
	/**
	 * Check if the player has that bending controller
	 */
	@Override
	public boolean hasBending(BendingController bending) {
		return bendings.contains(bending);
	}
	
	/**
	 * Check if the player has that type of bending
	 */
	@Override
	public boolean hasBending(int type) {
		return hasBending(BendingManager.getBending(type));
	}
	
	/**
	 * If the bending controller is not already present, adds the bending
	 * controller.
	 * <p>
	 * Also adds the state if it isn't present.
	 */
	@Override
	public void addBending(BendingController bending) {
		if (bendings.add(bending)) {
			save(DataCategory.BENDING_LIST);
		}
	}
	
	/**
	 * If the bending controller is not already present, adds the bending
	 * controller.
	 */
	@Override
	public void addBending(int type) {
		addBending(BendingManager.getBending(type));
	}
	
	/**
	 * Remove the specified bending controller and its associated state. Please
	 * note, this will be saved, so is permanent (unless another bending
	 * controller is added).
	 */
	@Override
	public void removeBending(BendingController bending) {
		if (bendings.remove(bending)) {
			save(DataCategory.BENDING_LIST);
		}
	}
	
	/**
	 * Remove the bending controller and its state with that type.
	 * 
	 * @see #removeBending(BendingController)
	 */
	@Override
	public void removeBending(int type) {
		removeBending(BendingManager.getBending(type));
	}
	
	@Override
	public List<BendingController> getAllBending() {
		return new ArrayList<>(bendings);
	}
	
	@Override
	public void setAllBending(List<BendingController> bending) {
		bendings.clear();
		bendings.addAll(bending);
	}
	
	@Override
	public void clearBending() {
		bendings.clear();
	}
	
	// ================================================================================
	// ACTIVE BENDING
	// ================================================================================
	
	@Override
	public BendingController getActiveBending() {
		if (!bendings.isEmpty() && activeBending == null) {
			activeBending = bendings.iterator().next();
		}
		if (activeBending != null && !bendings.isEmpty() && !bendings.contains(activeBending)) {
			activeBending = bendings.iterator().next();
		}
		if (bendings.isEmpty() && activeBending != null) {
			activeBending = null;
		}
		return activeBending;
	}
	
	@Override
	public int getActiveint() {
		BendingController controller = getActiveBending();
		return controller == null ? null : controller.getType();
	}
	
	@Override
	public void setActiveBending(BendingController controller) {
		if (!bendings.isEmpty() && bendings.contains(controller)) {
			activeBending = controller;
			save(DataCategory.ACTIVE_BENDING);
		}
	}
	
	@Override
	public void setActiveint(int type) {
		BendingController controller = BendingManager.getBending(type);
		setActiveBending(controller);
	}
	
	// ================================================================================
	// STATUS CONTROLS
	// ================================================================================
	
	@Override
	public boolean hasStatusControl(StatusControl control) {
		return statusControls.contains(control);
	}
	
	@Override
	public void addStatusControl(StatusControl control) {
		if (statusControls.add(control)) {
			save(DataCategory.STATUS_CONTROLS);
		}
	}
	
	@Override
	public void removeStatusControl(StatusControl control) {
		if (statusControls.remove(control)) {
			save(DataCategory.STATUS_CONTROLS);
		}
	}
	
	@Override
	public List<StatusControl> getAllStatusControls() {
		return new ArrayList<>(statusControls);
	}
	
	@Override
	public void setAllStatusControls(List<StatusControl> controls) {
		statusControls.clear();
		statusControls.addAll(controls);
	}
	
	@Override
	public void clearStatusControls() {
		statusControls.clear();
	}
	
	// ================================================================================
	// ABILITY DATA
	// ================================================================================
	
	@Override
	public boolean hasAbilityData(BendingAbility ability) {
		return abilityData.get(ability) != null;
	}
	
	/**
	 * Retrieves data about the given ability. Will create data if necessary.
	 */
	@Override
	public AbilityData getAbilityData(BendingAbility ability) {
		AbilityData data = abilityData.get(ability);
		if (data == null) {
			data = new AbilityData(this, ability);
			abilityData.put(ability, data);
			save(DataCategory.BENDING_LIST);
		}
		
		return data;
	}
	
	@Override
	public void setAbilityData(BendingAbility ability, AbilityData data) {
		abilityData.put(ability, data);
	}
	
	/**
	 * Gets a list of all ability data contained in this player data.
	 */
	@Override
	public List<AbilityData> getAllAbilityData() {
		return new ArrayList<>(abilityData.values());
	}
	
	@Override
	public Map<BendingAbility, AbilityData> getAbilityDataMap() {
		return new HashMap<>(abilityData);
	}
	
	@Override
	public void setAbilityDataMap(Map<BendingAbility, AbilityData> map) {
		abilityData.clear();
		abilityData.putAll(map);
	}
	
	/**
	 * Removes all ability data associations
	 */
	@Override
	public void clearAbilityData() {
		abilityData.clear();
	}
	
	// ================================================================================
	// CHI
	// ================================================================================
	
	/**
	 * Gets the chi information about the bender
	 */
	@Override
	public Chi chi() {
		return chi;
	}
	
	@Override
	public void setChi(Chi chi) {
		this.chi = chi;
		save(DataCategory.CHI);
	}
	
	// ================================================================================
	// TICK HANDLERS
	// ================================================================================
	
	@Override
	public boolean hasTickHandler(TickHandler handler) {
		return tickHandlers.contains(handler);
	}
	
	@Override
	public void addTickHandler(TickHandler handler) {
		if (tickHandlers.add(handler)) {
			save(DataCategory.TICK_HANDLERS);
		}
	}
	
	@Override
	public void removeTickHandler(TickHandler handler) {
		if (tickHandlers.remove(handler)) {
			save(DataCategory.TICK_HANDLERS);
		}
	}
	
	@Override
	public List<TickHandler> getAllTickHandlers() {
		return new ArrayList<>(tickHandlers);
	}
	
	@Override
	public void setAllTickHandlers(List<TickHandler> handlers) {
		tickHandlers.clear();
		tickHandlers.addAll(handlers);
	}
	
	@Override
	public void clearTickHandlers() {
		tickHandlers.clear();
	}
	
	// ================================================================================
	// MISC
	// ================================================================================
	
	@Override
	public MiscData getMiscData() {
		return miscData;
	}
	
	@Override
	public void setMiscData(MiscData md) {
		this.miscData = md;
	}
	
	@Override
	public float getFallAbsorption() {
		return miscData.getFallAbsorption();
	}
	
	@Override
	public void setFallAbsorption(float fallAbsorption) {
		miscData.setFallAbsorption(fallAbsorption);
	}
	
	@Override
	public int getTimeInAir() {
		return miscData.getTimeInAir();
	}
	
	@Override
	public void setTimeInAir(int time) {
		miscData.setTimeInAir(time);
	}
	
	@Override
	public int getAbilityCooldown() {
		return miscData.getAbilityCooldown();
	}
	
	@Override
	public void setAbilityCooldown(int cooldown) {
		miscData.setAbilityCooldown(cooldown);
	}
	
	@Override
	public void decrementCooldown() {
		miscData.decrementCooldown();
	}
	
	@Override
	public boolean isWallJumping() {
		return miscData.isWallJumping();
	}
	
	@Override
	public void setWallJumping(boolean wallJumping) {
		miscData.setWallJumping(wallJumping);
	}
	
	@Override
	public int getPetSummonCooldown() {
		return miscData.getPetSummonCooldown();
	}
	
	@Override
	public void setPetSummonCooldown(int cooldown) {
		miscData.setPetSummonCooldown(cooldown);
	}
	
	/**
	 * Save this BendingData
	 */
	@Override
	public abstract void save(DataCategory category);
	
}
