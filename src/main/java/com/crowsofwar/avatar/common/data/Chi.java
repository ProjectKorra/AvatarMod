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
	
	// These fields are not for modification directly; use getters/setters
	private float max;
	private float current;
	private float availableMark;
	
	public Chi() {
		// Default values for testing
		this.max = 20;
		this.current = 10;
		this.availableMark = 8;
	}
	
	/**
	 * Gets the current amount of chi. Some may not be usable.
	 * 
	 * @see #setTotalChi(float)
	 */
	public float getTotalChi() {
		return current;
	}
	
	/**
	 * Sets the current amount of chi. Some may not be usable.
	 * 
	 * @see #getTotalChi()
	 */
	public void setTotalChi(float total) {
		this.current = total;
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
	}
	
	/**
	 * Gets the current available amount of chi.
	 * 
	 * @see #setAvailableChi(float)
	 */
	public float getAvailableChi() {
		return current - availableMark;
	}
	
	/**
	 * Moves the available chi mark so the amount of available chi is now at the
	 * requested value.
	 * 
	 * @see #getAvailableChi()
	 */
	public void setAvailableChi(float available) {
		this.availableMark = current - available;
	}
	
	/**
	 * Gets the maximum amount of available chi, at this available mark
	 */
	public float getAvailableMaxChi() {
		return max - availableMark;
	}
	
}
