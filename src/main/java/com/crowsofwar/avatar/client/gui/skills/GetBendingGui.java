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

import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.client.uitools.UiComponentHandler;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.ContainerGetBending;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class GetBendingGui extends GuiContainer implements AvatarGui {
	
	private final ContainerGetBending container;
	
	private final UiComponentHandler handler;
	private final UiComponent[] componentScrollSlots;
	private final UiComponent componentInventory;
	
	public GetBendingGui(EntityPlayer player) {
		super(new ContainerGetBending(player));
		this.container = (ContainerGetBending) inventorySlots;
		
		handler = new UiComponentHandler();
		
		Frame slotsFrame = new Frame();
		slotsFrame.setPosition(Measurement.fromPercent((100 - 30) / 2, 10));
		slotsFrame.setDimensions(Measurement.fromPercent(30, 80));
		
		componentScrollSlots = new UiComponent[container.getSize()];
		for (int i = 0; i < componentScrollSlots.length; i++) {
			
			ComponentInventorySlots comp = new ComponentInventorySlots(container, i);
			comp.setFrame(frame);
			
			componentScrollSlots[i] = comp;
			handler.add(comp);
			
		}
		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
	}
	
}
