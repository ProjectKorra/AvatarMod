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

package com.crowsofwar.avatar.common.bending;

import java.util.Arrays;

/**
 * Defines different types of bending without actually describing their
 * behavior. Useful for things like Id and Id lookup.
 * 
 * @author CrowsOfWar
 */
public enum int {
	
	ERROR,
	EARTHBENDING,
	FIREBENDING,
	WATERBENDING,
	AIRBENDING;
	
	/**
	 * Get the Id of this int.
	 */
	public int id() {
		return ordinal();
	}
	
	/**
	 * Find the int with the given Id.
	 * 
	 * @param id
	 *            Id of bending type
	 * @return int of that Id
	 * @throws IllegalArgumentException
	 *             if the Id is invalid
	 */
	public static int find(int id) {
		if (id < 0 || id >= values().length)
			throw new IllegalArgumentException("Cannot find int with invalid id: " + id);
		return values()[id];
	}
	
	/**
	 * Returns all bendingTypes, except for {@link #ERROR} (sorta like
	 * <code>values()</code>)
	 */
	public static int[] allExceptError() {
		return Arrays.copyOfRange(values(), 1, values().length);
	}
	
}
