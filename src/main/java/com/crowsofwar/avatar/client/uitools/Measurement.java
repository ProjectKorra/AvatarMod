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
 * A measurement that can either be in screen pixels or percentage of total
 * screen.
 * 
 * @author CrowsOfWar
 */
public class Measurement {
	
	private final float pixels;
	private final int maxPixels;
	
	private Measurement(float px, int maxPixels) {
		this.pixels = px;
		this.maxPixels = maxPixels;
	}
	
	/**
	 * Returns the measurement in pixels.
	 */
	public float inPixels() {
		return pixels;
	}
	
	/**
	 * Returns the measurement in percentage of screen width/height from 0-100.
	 */
	public float inPercent() {
		return pixels / maxPixels * 100;
	}
	
	public static Measurement fromHorizPixels(float px) {
		return new Measurement(px, screenWidth());
	}
	
	public static Measurement fromVertPixels(float px) {
		return new Measurement(px, screenHeight());
	}
	
	/**
	 * Percent is from 0-100.
	 */
	public static Measurement fromHorizPct(float percent) {
		return new Measurement(screenWidth() * percent / 100, screenWidth());
	}
	
	/**
	 * Percent is from 0-100.
	 */
	public static Measurement fromVertPct(float percent) {
		return new Measurement(screenHeight() * percent / 100, screenHeight());
	}
	
}
