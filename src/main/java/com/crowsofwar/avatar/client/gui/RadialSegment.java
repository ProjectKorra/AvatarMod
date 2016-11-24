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

package com.crowsofwar.avatar.client.gui;

import net.minecraft.client.gui.ScaledResolution;

/**
 * Holds information for the RadialMenu about a segment. Contains information on its rotation
 * (position), and whether it's clicked.
 *
 */
public class RadialSegment {
	
	private final RadialMenu gui;
	private final float angle;
	private final int index;
	private final int icon;
	
	public RadialSegment(RadialMenu gui, int index, int icon) {
		this.gui = gui;
		this.angle = 22.5f + index * 45;
		this.index = index;
		if (icon == -1) icon = 255;
		this.icon = icon;
	}
	
	/**
	 * Returns whether the mouse is currently hovering
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public boolean isMouseHover(int mouseX, int mouseY, ScaledResolution resolution) {
		
		int mouseCenteredX = mouseX - resolution.getScaledWidth() / 2;
		int mouseCenteredY = mouseY - resolution.getScaledHeight() / 2;
		double r = Math.sqrt(mouseCenteredX * mouseCenteredX + mouseCenteredY * mouseCenteredY)
				/ RadialMenu.menuScale;
		double currentAngle = Math.toDegrees(Math.atan2(mouseCenteredY, mouseCenteredX)) + 90;
		double minAngle = angle - 44;
		if (minAngle < 0) minAngle += 360;
		double maxAngle = angle;
		boolean addCurrentAngle = currentAngle < 0;
		if (minAngle > maxAngle) {
			maxAngle += 360;
			addCurrentAngle = true;
		}
		if (addCurrentAngle) currentAngle += 360;
		
		return r >= 100 && r <= 200 && currentAngle >= minAngle && currentAngle <= maxAngle;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public int getTextureU() {
		return (icon * 32) % 256;
	}
	
	public int getTextureV() {
		return (icon / 8) * 32;
	}
	
}
