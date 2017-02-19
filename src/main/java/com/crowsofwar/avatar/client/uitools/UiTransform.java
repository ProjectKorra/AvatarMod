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
package com.crowsofwar.avatar.client.uitools;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public interface UiTransform {
	
	/**
	 * The calculated x-position based on position and offset
	 */
	Measurement x();
	
	/**
	 * The calculated y-position based on position and offset
	 */
	Measurement y();
	
	/**
	 * The starting position, such as top-right
	 */
	StartingPosition position();
	
	/**
	 * Returns the x-offset from the {@link #position() starting position}.
	 */
	Measurement offsetX();
	
	/**
	 * Returns the y-offset from the {@link #position() starting position}.
	 */
	Measurement offsetY();
	
	/**
	 * Returns the value to multiply by the {@link #offsetX() offsets}.
	 */
	float offsetScale();
	
}
