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
package com.crowsofwar.avatar.client.gui.skills;

import com.crowsofwar.avatar.client.uitools.UiComponent;

import net.minecraft.util.ResourceLocation;

/**
 * A component which allows control over a gui-container's Slots. Makes it
 * easier to set position, background, visibility, etc.
 * 
 * @author CrowsOfWar
 */
public class ComponentInventorySlots extends UiComponent {
	
	private final int cols, rows;
	private int width, height;
	
	private ResourceLocation texture;
	private int u, v;
	
	/**
	 * Creates a grid of inventory slots with the given dimensions. This assumes
	 * the indices are in row-major order.
	 * 
	 * @param cols
	 *            The number of columns (x) in this grid
	 * @param rows
	 *            The number of rows (y) in this grid
	 * @param minIndex
	 *            Minimum slot index
	 * @param maxIndex
	 *            Maximum slot index
	 */
	public ComponentInventorySlots(int cols, int rows, int minIndex, int maxIndex) {
		this.cols = cols;
		this.rows = rows;
		this.width = cols * 18;
		this.height = rows * 18;
		
		this.texture = null;
		this.u = -1;
		this.v = -1;
		
	}
	
	/**
	 * Draws using the given texture. Dimensions will also be updated.
	 */
	public void useTexture(ResourceLocation texture, int u, int v, int width, int height) {
		this.width = width;
		this.height = height;
		this.texture = texture;
		this.u = u;
		this.v = v;
	}
	
	@Override
	protected float componentWidth() {
		return 0;
	}
	
	@Override
	protected float componentHeight() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	protected void componentDraw(float partialTicks) {
		// TODO Auto-generated method stub
		
	}
	
}
