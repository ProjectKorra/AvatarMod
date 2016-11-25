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

package com.crowsofwar.avatar.common.gui;

/**
 * Represents an icon for an ability. Icons are 32x32px and found on the
 * ability_icons spritesheet (
 * <code>assets/avatarmod/textures/gui/ability_icons.png</code>).
 * 
 * @author CrowsOfWar
 */
public class AbilityIcon {
	
	private final int u;
	private final int v;
	
	/**
	 * Creates the icon with the given spritesheet index. Indices start at 0 and
	 * go left-to-right. 0 is the first icon, 1 is the second, etc...
	 */
	public AbilityIcon(int index) {
		if (index < 0 || index > 255)
			throw new IndexOutOfBoundsException("Icons must be between 0 and 255, inclusive");
		this.u = (index * 32) % 256;
		this.v = (index / 8) * 32;
	}
	
	public int getMinU() {
		return u;
	}
	
	public int getMaxU() {
		return u + 32;
	}
	
	public int getMinV() {
		return v;
	}
	
	public int getMaxV() {
		return v + 32;
	}
	
}
