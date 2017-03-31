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

import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AnimalCondition {
	
	private final EntityCreature animal;
	
	private float lastDistance;
	
	private float maxFoodPoints;
	private float foodPoints;
	
	public AnimalCondition(EntityCreature animal, float maxFoodPoints) {
		this.animal = animal;
		lastDistance = animal.distanceWalkedModified;
		foodPoints = maxFoodPoints;
	}
	
	public void onUpdate() {
		float distance = animal.distanceWalkedModified;
		float diff = distance - lastDistance;
		addHunger(diff);
		System.out.println("food points currently : " + foodPoints);
		
		lastDistance = distance;
	}
	
	public float getSpeedMultiplier() {
		
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
		foodPoints += food;
		if (foodPoints < 0) {
			foodPoints = 0;
		}
		if (foodPoints > maxFoodPoints) {
			foodPoints = maxFoodPoints;
		}
	}
	
	public void writeToNbt(NBTTagCompound nbt) {
		
	}
	
	public void readFromNbt(NBTTagCompound nbt) {
		
	}
	
}
