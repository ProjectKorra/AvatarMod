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
package com.crowsofwar.avatar.entity.data;

import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;

import static com.crowsofwar.avatar.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AnimalCondition {

	private final DataParameter<Float> syncFood;
	private final DataParameter<Integer> syncDomestication;
	private final DataParameter<Integer> syncAge;
	private final EntityCreature animal;
	private final float maxFoodPoints, foodRegenPoints;

	private float lastDistance;
	private int breedTimer;
	private boolean sterile;

	public AnimalCondition(EntityCreature animal, float maxFoodPoints, float foodRegenPoints,
						   DataParameter<Float> syncFood, DataParameter<Integer> syncDomestication,
						   DataParameter<Integer> syncAge) {
		this.animal = animal;
		this.syncDomestication = syncDomestication;
		this.syncFood = syncFood;
		this.syncAge = syncAge;
		this.maxFoodPoints = maxFoodPoints;
		this.foodRegenPoints = foodRegenPoints;

		this.lastDistance = animal.distanceWalkedModified;
		this.breedTimer = -1;
		this.sterile = true;

	}

	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("FoodPoints", getFoodPoints());
		nbt.setInteger("Domestication", getDomestication());
		nbt.setInteger("Age", getAge());
		nbt.setInteger("BreedTimer", getBreedTimer());
		nbt.setBoolean("Sterile", isSterile());
	}

	public void readFromNbt(NBTTagCompound nbt) {
		setFoodPoints(nbt.getFloat("FoodPoints"));
		setDomestication(nbt.getInteger("Domestication"));
		setAge(nbt.getInteger("Age"));
		setSterile(nbt.getBoolean("Sterile"));
		// must call setBreedTimer after setSterile because sterile affects sbt
		setBreedTimer(nbt.getInteger("BreedTimer"));
	}

	public void onUpdate() {
		float distance = animal.distanceWalkedModified;
		// Rarely, an error can occur where distance is NaN (divide by 0)
		if (Float.isNaN(distance)) {
			distance = lastDistance;
		}
		float diff = distance - lastDistance;
		addHunger(diff * 0.05f);

		lastDistance = distance;

		if (!animal.world.isRemote) {
			boolean enoughFood = getFoodPoints() >= foodRegenPoints;
			boolean correctTime = animal.ticksExisted % 40 == 0;
			if (enoughFood && correctTime) {
				animal.heal(1);
				addHunger(1);
			}
			addAge(1);
			if (!isSterile() && isAdult()) {
				addBreedTimer(-1);
			}
		}

	}

	// ================================================================================
	// DOMESTICATION
	// ================================================================================

	public int getDomestication() {
		return animal.getDataManager().get(syncDomestication);
	}

	public void setDomestication(int domestication) {
		if (domestication < 0) domestication = 0;
		if (domestication > 1000) domestication = 1000;
		animal.getDataManager().set(syncDomestication, domestication);
	}

	public void addDomestication(int domestication) {
		setDomestication(getDomestication() + domestication);
	}

	public boolean canHaveOwner() {
		return getDomestication() >= MOBS_CONFIG.bisonSettings.bisonOwnableTameness;
	}

	public int getMaxRiders() {

		//As the max adult age is 7, it'll have a max of 7 riders.
		if (getAgeDays() >= (int) (getAdultAge() / getAgeDays())) {
			return 1 + (int) Math.min(getAgeDays() - (int) (getAdultAge() / getAgeDays()), getAdultAge());
		}

		if (canHaveOwner()) {

			double pctToTame = 1.0 * (getDomestication() - MOBS_CONFIG.bisonSettings.bisonOwnableTameness)
					/ (1000 - MOBS_CONFIG.bisonSettings.bisonOwnableTameness);
			return 1 + (int) (pctToTame * 4);

		} else if (getDomestication() >= MOBS_CONFIG.bisonSettings.bisonRiderTameness) {
			return 1;
		} else {
			return 0;
		}
	}

	public boolean isFullyDomesticated() {
		return getDomestication() == 1000;
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

	// ================================================================================
	// AGE
	// ================================================================================

	/**
	 * Gets the age in ticks
	 */
	public int getAge() {
		return animal.getDataManager().get(syncAge);
	}

	public void setAge(int age) {
		if (age < 0) age = 0;
		animal.getDataManager().set(syncAge, age);
	}

	public void addAge(int age) {
		setAge(getAge() + age);
	}

	public float getAgeDays() {
		return getAge() / 24000f;
	}

	public void setAgeDays(float days) {
		setAge((int) (days * 24000));
	}

	public float getSizeMultiplier() {
		//TODO: Fix size maths
		float adultSize = getAgeDays() < 5 ? getAgeDays() / getAdultAge() : 1.666777f;
		return isAdult() ? adultSize : 0.1f + getAgeDays() / getAdultAge() * 0.9f;
	}

	public boolean isAdult() {
		return getAgeDays() >= getAdultAge();
	}

	/**
	 * Returns the number of days it takes to become an adult.
	 */
	public int getAdultAge() {
		return 7;
	}

	// ================================================================================
	// BREEDING
	// ================================================================================

	/**
	 * Get the breed timer. If {@link #isSterile() is sterile}, returns -1.
	 */
	public int getBreedTimer() {
		return sterile ? -1 : breedTimer;
	}

	/**
	 * Set the breedTimer. Ignored if {@link #isSterile() is sterile}.
	 */
	public void setBreedTimer(int breedTimer) {
		if (!isSterile()) {
			if (breedTimer < 0) breedTimer = 0;
			this.breedTimer = breedTimer;
		}
	}

	public void addBreedTimer(int amount) {
		setBreedTimer(getBreedTimer() + amount);
	}

	/**
	 * Get whether the bison is ready to breed right now.
	 */
	public boolean isReadyToBreed() {
		return isAdult() && getBreedTimer() == 0;
	}

	public boolean isSterile() {
		return sterile;
	}

	public void setSterile(boolean sterile) {
		this.sterile = sterile;
	}

}
