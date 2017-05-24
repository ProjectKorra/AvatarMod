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

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.scaleFactor;
import static net.minecraft.client.renderer.GlStateManager.color;

import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.UiComponent;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

/**
 * A component which allows control over a gui-container's Slots. Makes it
 * easier to set position, background, visibility, etc.
 * 
 * @author CrowsOfWar
 */
public class ComponentInventorySlots extends UiComponent {
	
	private final Container container;
	
	private final int cols, rows, minIndex, maxIndex;
	private int width, height;
	
	private ResourceLocation texture;
	private int u, v;
	
	private Measurement padding;
	
	/**
	 * Creates only one inventory slot at the given index.
	 */
	public ComponentInventorySlots(Container container, int index) {
		this(container, 1, 1, index, index);
	}
	
	/**
	 * Creates a grid of inventory slots with the given dimensions. This assumes
	 * the indices are in row-major order. Min/max index are inclusive.
	 */
	public ComponentInventorySlots(Container container, int cols, int rows, int minIndex, int maxIndex) {
		
		this.container = container;
		
		this.cols = cols;
		this.rows = rows;
		this.width = cols * 18;
		this.height = rows * 18;
		this.minIndex = minIndex;
		this.maxIndex = maxIndex;
		
		this.texture = null;
		this.u = -1;
		this.v = -1;
		
		this.padding = Measurement.fromPixels(0, 0);
		
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
		return width;
	}
	
	@Override
	protected float componentHeight() {
		return height;
	}
	
	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {
		// Check visibility
		for (int i = minIndex; i <= maxIndex; i++) {
			Slot slot = container.getSlot(i);
			int x = (int) coordinates().xInPixels() + (int) padding.xInPixels() * scaleFactor();
			int y = (int) coordinates().yInPixels() + (int) padding.yInPixels() * scaleFactor();
			
			int j = i - minIndex;
			
			slot.xDisplayPosition = 18 * scaleFactor() * (j % cols) + x;
			slot.yDisplayPosition = 18 * scaleFactor() * (j / cols) + y;
			slot.xDisplayPosition /= scaleFactor();
			slot.yDisplayPosition /= scaleFactor();
			slot.xDisplayPosition++;
			slot.yDisplayPosition++;
		}
		
		// Draw texture
		if (texture != null) {
			mc.renderEngine.bindTexture(texture);
			color(1, 1, 1, 1);
			drawTexturedModalRect(0, 0, u, v, width, height);
		}
		
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!isVisible()) {
			for (int i = minIndex; i <= maxIndex; i++) {
				Slot slot = container.getSlot(i);
				slot.xDisplayPosition = -18;
				slot.yDisplayPosition = -18;
			}
		}
	}
	
	public Measurement getPadding() {
		return padding;
	}
	
	/**
	 * Set offset from texture to start of slots
	 */
	public void setPadding(Measurement padding) {
		this.padding = padding;
	}
	
}
