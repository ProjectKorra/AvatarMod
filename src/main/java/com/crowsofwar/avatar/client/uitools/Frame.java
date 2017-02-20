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

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenHeight;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenWidth;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class Frame {
	
	public Frame SCREEN = new Frame(null) {
		
		@Override
		public Measurement getOffset() {
			return Measurement.fromPixels(0, 0);
		}
		
		@Override
		public Measurement getDimensions() {
			return Measurement.fromPixels(screenWidth(), screenHeight());
		}
		
	};
	
	private final Frame parent;
	private Measurement position, dimensions;
	
	public Frame(Frame parent) {
		this.parent = parent;
		this.position = Measurement.fromPixels(0, 0);
		this.dimensions = Measurement.fromPixels(screenWidth(), screenHeight());
	}
	
	/**
	 * Get offset within the frame
	 */
	public Measurement getOffset() {
		return position;
	}
	
	public void setPosition(Measurement position) {
		this.position = position;
	}
	
	public Measurement getDimensions() {
		return dimensions;
	}
	
	public void setDimensions(Measurement dimensions) {
		this.dimensions = dimensions;
	}
	
	/**
	 * Get the calculated minimum coordinates of this frame
	 */
	public Measurement getCoordsMin() {
		return getOffset().plus(parent.getCoordsMin());
	}
	
}
