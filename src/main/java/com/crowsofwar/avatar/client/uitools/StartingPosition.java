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
public class StartingPosition {
	
	public static StartingPosition TOP_LEFT = new StartingPosition(0, 0, 0, 0);
	public static StartingPosition TOP_RIGHT = new StartingPosition(1, 0, 1, 0),
			TOP_CENTER = new StartingPosition(.5f, 0, .5f, 0),
			MIDDLE_TOP = new StartingPosition(.5f, 0, .5f, 0),
			MIDDLE_CENTER = new StartingPosition(.5f, .5f, .5f, .5f),
			MIDDLE_BOTTOM = new StartingPosition(.5f, 1, .5f, 1),
			BOTTOM_RIGHT = new StartingPosition(1, 1, 1, 1);
	
	private float x, y, minusX, minusY;
	
	private StartingPosition(float x, float y, float minusX, float minusY) {
		this.x = x;
		this.y = y;
		this.minusX = minusX;
		this.minusY = minusY;
	}
	
	/**
	 * Gets amount of x divided by the frame width (0-1)
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Gets amount of y divided by the frame height (0-1)
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * To achieve the desired x coordinate, the width times this number should
	 * be subtracted from the x position.
	 */
	public float getMinusX() {
		return minusX;
	}
	
	/**
	 * To achieve the desired y coordinate, the height times this number should
	 * be subtracted from the y position.
	 */
	public float getMinusY() {
		return minusY;
	}
	
	public static StartingPosition custom(float x, float y) {
		return new StartingPosition(x, y, .5f, .5f);
	}
	
	public static StartingPosition custom(float x, float y, float mx, float my) {
		return new StartingPosition(x, y, mx, my);
	}
	
}
