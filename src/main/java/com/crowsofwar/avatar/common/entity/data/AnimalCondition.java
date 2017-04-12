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
package com.crowsofwar.avatar.common.entity.data;

import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AnimalCondition {
	
	private final DataParameter<Float> syncFood;
	private final EntityCreature animal;
	private final float maxFoodPoints, foodRegenPoints;
	
	private float lastDistance;
	private int domestication;
	
	public AnimalCondition(EntityCreature animal, float maxFoodPoints, float foodRegenPoints,
			DataParameter<Float> syncFood, int domestication) {
		this.animal = animal;
		this.syncFood = syncFood;
		this.maxFoodPoints = maxFoodPoints;
		this.foodRegenPoints = foodRegenPoints;
		
		this.lastDistance = animal.distanceWalkedModified;
		this.domestication = domestication;
		
	}
	
	public void onUpdate() {
		float distance = animal.distanceWalkedModified;
		float diff = distance - lastDistance;
		addHunger(diff * 0.1f);
		
		lastDistance = distance;
		
		if (!animal.worldObj.isRemote) {
			boolean enoughFood = getFoodPoints() >= foodRegenPoints;
			boolean correctTime = animal.ticksExisted % 40 == 0;
			if (enoughFood && correctTime) {
				animal.heal(1);
				addHunger(1);
			}
		}
		
	}
	
	// ================================================================================
	// DOMESTICATION
	// ================================================================================
	
	public int getDomestication() {
		return domestication;
		
	}
	
	public void setDomestication(int domestication) {
		if (domestication < 0) domestication = 0;
		if (domestication > 1000) domestication = 1000;
		this.domestication = domestication;
	}
	
	public void addDomestication(int domestication) {
		setDomestication(this.domestication + domestication);
	}
	
	public boolean canHaveOwner() {
		return domestication >= MOBS_CONFIG.bisonOwnableTameness;
	}
	
	public int getMaxRiders() {
		if (canHaveOwner()) {
			
			double pctToTame = 1.0 * (domestication - MOBS_CONFIG.bisonOwnableTameness)
					/ (1000 - MOBS_CONFIG.bisonOwnableTameness);
			return 1 + (int) (pctToTame * 4);
			
		} else if (domestication >= MOBS_CONFIG.bisonRiderTameness) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public boolean isFullyDomesticated() {
		return domestication == 0;
	}
	
	// ================================================================================
	// FOOD POINTS
	// ================================================================================
	
	public float getFoodPoints() {
		return animal.getDataManager().get(syncFood);
	}
	
	public void setFoodPoints(float points) {
		animal.getDataManager().set(syncFood, points);
	}
	
	/**
	 * Make the animal hungrier by the given amount (subtracting from
	 * foodPoints)
	 * 
	 * @see #addFood(float)
	 */
	public void addHunger(float hunger) {
		addFood(-hunger);
	}
	
	/**
	 * Adds food points to the animal (adding to foodPoints).
	 */
	public void addFood(float food) {
		float foodPoints = getFoodPoints();
		foodPoints += food;
		if (foodPoints < 0) {
			foodPoints = 0;
		}
		if (foodPoints > maxFoodPoints) {
			foodPoints = maxFoodPoints;
		}
		setFoodPoints(foodPoints);
	}
	
	public float getSpeedMultiplier() {
		return 0.6f + 0.4f * getFoodPoints() / maxFoodPoints;
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("FoodPoints", getFoodPoints());
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		setFoodPoints(nbt.getFloat("FoodPoints"));
	}
	
}
