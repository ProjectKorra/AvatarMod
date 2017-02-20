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

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.*;
import static net.minecraft.client.renderer.GlStateManager.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

/**
 * A positioned rectangular frame within the screen which allows positioning of
 * {@link UiComponent components} within a certain bounds.
 * 
 * @author CrowsOfWar
 */
public class Frame extends Gui {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod", "textures/gui/box.png");
	
	public static final Frame SCREEN = new Frame(null) {
		
		@Override
		public Measurement getOffset() {
			return Measurement.fromPixels(0, 0);
		}
		
		@Override
		public Measurement getDimensions() {
			return Measurement.fromPixels(screenWidth(), screenHeight());
		}
		
		@Override
		public Measurement getCoordsMin() {
			return Measurement.fromPixels(0, 0);
		}
		
	};
	
	private final Frame parent;
	private Measurement offset, dimensions;
	
	public Frame() {
		this(SCREEN);
	}
	
	public Frame(Frame parent) {
		this.parent = parent;
		this.offset = Measurement.fromPixels(0, 0);
		this.dimensions = Measurement.fromPixels(screenWidth(), screenHeight());
	}
	
	/**
	 * Get offset from the parent frame
	 */
	public Measurement getOffset() {
		return offset;
	}
	
	public void setPosition(Measurement offset) {
		this.offset = offset;
	}
	
	public Measurement getDimensions() {
		return dimensions;
	}
	
	public void setDimensions(Measurement dimensions) {
		this.dimensions = dimensions;
	}
	
	/**
	 * Get the calculated coordinates of this frame, based off of parent pos +
	 * offset. Top-left.
	 */
	public Measurement getCoordsMin() {
		return getOffset().plus(parent.getCoordsMin());
	}
	
	/**
	 * Get the calculated coordinates of this frame, based off of parent pos +
	 * offset. Bottom-right.
	 */
	public Measurement getCoordsMax() {
		return getCoordsMin().plus(getDimensions());
	}
	
	public void draw(float partialTicks) {
		//@formatter:off
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		pushMatrix();
			
			enableBlend();
		
			scaleFactor();
			
			scale(1f / scaleFactor(), 1f / scaleFactor(), 1);
			translate(getCoordsMin().xInPixels(), getCoordsMin().yInPixels(), 0);
			scale(dimensions.xInPixels() / 256, dimensions.yInPixels() / 256, 1);
			drawTexturedModalRect(0, 0, 0, 0, 256, 256);
			
			disableBlend();
			
		popMatrix();
		//@formatter:on
	}
	
}
