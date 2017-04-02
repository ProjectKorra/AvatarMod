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
import static net.minecraft.client.Minecraft.getMinecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * Handles calls to all UI components, so they don't need to be worried about
 * outside their instantiation.
 * 
 * @author CrowsOfWar
 */
public class UiComponentHandler {
	
	private final List<UiComponent> components;
	
	public UiComponentHandler() {
		components = new ArrayList<>();
	}
	
	public UiComponentHandler(UiComponent... components) {
		this();
		this.components.addAll(Arrays.asList(components));
	}
	
	public void add(UiComponent component) {
		components.add(component);
	}
	
	public void draw(float partialTicks, float mouseX, float mouseY) {
		
		List<String> tooltip = null;
		
		for (UiComponent component : components) {
			component.draw(partialTicks);
			
			float mx2 = Mouse.getX();
			float my2 = screenHeight() - Mouse.getY();
			
			Measurement coords = component.coordinates();
			if (mx2 >= coords.xInPixels() && mx2 <= coords.xInPixels() + component.width()) {
				if (my2 >= coords.yInPixels() && my2 <= coords.yInPixels() + component.height()) {
					List<String> result = component.getTooltip(mx2, my2);
					if (result != null) {
						tooltip = result;
					}
				}
			}
			
		}
		
		if (tooltip != null) {
			
			int width = screenWidth() / scaleFactor();
			int height = screenHeight() / scaleFactor();
			
			GuiUtils.drawHoveringText(tooltip, (int) mouseX, (int) mouseY, width, height, -1,
					getMinecraft().fontRendererObj);
			
		}
		
	}
	
	public void click(float x, float y, int button) {
		for (UiComponent component : components)
			component.mouseClicked(x, y, button);
	}
	
	public void type(int key) {
		for (UiComponent component : components)
			component.keyPressed(key);
	}
	
}
