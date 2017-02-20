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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ScreenInfo {
	
	private static int width, height, scale;
	
	public static int screenWidth() {
		return width;
	}
	
	public static int screenHeight() {
		return height;
	}
	
	public static int scaleFactor() {
		return scale == 0 ? 1 : scale;
	}
	
	public static void refreshDimensions() {
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		width = res.getScaledWidth() * res.getScaleFactor();
		height = res.getScaledHeight() * res.getScaleFactor();
		scale = res.getScaleFactor();
	}
	
}
