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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	public void draw(float partialTicks) {
		for (UiComponent component : components)
			component.draw(partialTicks);
	}
	
	public void click(float x, float y, int button) {
		
	}
	
}
