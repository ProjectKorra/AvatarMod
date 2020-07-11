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
package com.crowsofwar.avatar.util.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;

/**
 * Represents a bender's energy to use abilities. Chi is required to execute an
 * ability and also will regenerate over time.
 * <p>
 * Chi is somewhat simple; it is a bar with a current and maximum amount.
 * However, only a certain portion of the bar is usable at one time. This is
 * referred to as {@link #getAvailableChi() available chi}. The other chi can't
 * be used, until the available mark increases.
 *
 * @author CrowsOfWar
 */
public class Chi {

	private final BendingData data;

	// These fields are not for modification directly; use getters/setters
	private float max;
	private float total;
	private float availableMark;

	public Chi(BendingData data) {
		this.data = data;

		// Default values for testing
		this.max = 20;
		this.total = 10;
		this.availableMark = 8;

	}

	/**
	 * Gets the current amount of chi. Some may not be usable.
	 *
	 * @see #setTotalChi(float)
	 */
	public float getTotalChi() {
		return total;
	}

	/**
	 * Sets the current amount of chi. Some may not be usable.
	 *
	 * @see #getTotalChi()
	 */
	public void setTotalChi(float total) {
		this.total = total;
		save();
	}

	/**
	 * Adds the given amount of chi. The available chi is not affected. Accepts
	 * negative amounts (subtraction).
	 */
	public void changeTotalChi(float amount) {
		float prev = total;
		if (total + amount > max) {
			total = max;
		} else if (total + amount < 0) {
			total = 0;
		} else {
			total += amount;
		}
		availableMark += total - prev;
		if (Math.abs(availableMark) > total)
			availableMark = 0;
		save();
	}

	/**
	 * Gets the maximum amount of chi possible. However, not all of this chi
	 * would be usable at one time
	 *
	 * @see #setMaxChi(float)
	 */
	public float getMaxChi() {
		return max;
	}

	/**
	 * Sets the maximum amount of chi possible. However, not all of this chi
	 * would be usable at one time
	 *
	 * @see #getMaxChi()
	 */
	public void setMaxChi(float max) {
		this.max = max;
		if (max < total) setTotalChi(max);
		save();
	}

	/**
	 * Gets the current available amount of chi.
	 *
	 * @see #setAvailableChi(float)
	 */
	public float getAvailableChi() {
		return total - availableMark;
	}

	/**
	 * Moves the available chi mark so the amount of available chi is now at the
	 * requested value.
	 *
	 * @see #getAvailableChi()
	 */
	public void setAvailableChi(float available) {
		if (available > total) available = total;
		this.availableMark = total - available;
		save();
	}

	/**
	 * Adds the given amount of available chi. The total chi is not affected
	 * (just moves available chi mark).
	 */
	public void changeAvailableChi(float amount) {
		setAvailableChi(getAvailableChi() + amount);
	}

	/**
	 * Gets the maximum amount of available chi, at this available mark
	 */
	public float getAvailableMaxChi() {
		return max - availableMark;
	}

	/**
	 * <strong>Only designed for use by internal data classes.</strong> A {@link Bender} object
	 * is really the one responsible for consuming chi; use Bender{@link #consumeChi(float)}
	 * which also takes into account special conditions like <strong>creative mode</strong>.
	 * <p>
	 * Tries to consume the amount of available chi from the available and total pools; returns
	 * whether there was enough. Ignores special conditions (eg creative mode).
	 */
	public boolean consumeChi(float amount) {
		float available = getAvailableChi();
		if (available >= amount) {
			changeTotalChi(-amount);
			changeAvailableChi(-amount);
			return true;
		}
		return false;
	}

	private void save() {
		checkConsistency();
		data.save(DataCategory.CHI);
	}

	/**
	 * Ensures that variables do not conflict with each other or otherwise are
	 * invalid
	 */
	private void checkConsistency() {
		if (total < 0) total = 0;
		if (total > max) total = max;

		if (availableMark > total) availableMark = total;
		if (availableMark < 0) availableMark = 0;
	}

	/**
	 * Reads the chi information to NBT. This creates a subcompound in the
	 * parameter
	 */
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagCompound nbt = nestedCompound(compound, "ChiData");
		this.max = nbt.getFloat("Max");
		this.total = nbt.getFloat("Current");
		this.availableMark = nbt.getFloat("AvailableMark");
		if (max == 0) {
			max = 20;
			total = 10;
			availableMark = 8;
		}
	}

	/**
	 * Writes the chi information to NBT. This creates a subcompound in the
	 * parameter
	 */
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagCompound nbt = nestedCompound(compound, "ChiData");
		nbt.setFloat("Max", max);
		nbt.setFloat("Current", total);
		nbt.setFloat("AvailableMark", availableMark);
	}

	public void toBytes(ByteBuf buf) {
		buf.writeFloat(max);
		buf.writeFloat(total);
		buf.writeFloat(availableMark);
	}

	public void fromBytes(ByteBuf buf) {
		max = buf.readFloat();
		total = buf.readFloat();
		availableMark = buf.readFloat();
	}

}
