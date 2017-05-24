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

import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.ComponentText;
import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.ScreenInfo;
import com.crowsofwar.avatar.client.uitools.StartingPosition;
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.client.uitools.UiComponentHandler;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.ContainerGetBending;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class GetBendingGui extends GuiContainer implements AvatarGui {
	
	private final ContainerGetBending container;
	
	private final UiComponentHandler handler;
	private final UiComponent componentTitle;
	private final ComponentInventorySlots componentScrollSlots;
	private final ComponentInventorySlots componentInventory, componentHotbar;
	
	public GetBendingGui(EntityPlayer player) {
		super(new ContainerGetBending(player));
		this.container = (ContainerGetBending) inventorySlots;
		
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		xSize = res.getScaledWidth();
		ySize = res.getScaledHeight();
		
		handler = new UiComponentHandler();
		
		Frame slotsFrame = new Frame();
		slotsFrame.setPosition(Measurement.fromPercent((100 - 30) / 2, 10));
		slotsFrame.setDimensions(Measurement.fromPercent(30, 20));
		
		componentTitle = new ComponentText(TextFormatting.BOLD + I18n.format("avatar.getBending.title"));
		componentTitle.setFrame(slotsFrame);
		componentTitle.setPosition(StartingPosition.TOP_CENTER);
		componentTitle.setScale(1.5f);
		handler.add(componentTitle);
		
		componentScrollSlots = new ComponentInventorySlots(container, 3, 1, 0, 2);
		componentScrollSlots.setFrame(slotsFrame);
		componentScrollSlots.setPosition(StartingPosition.MIDDLE_BOTTOM);
		componentScrollSlots.setOffset(Measurement.fromPixels(slotsFrame, 0, componentTitle.height()));
		componentScrollSlots.useTexture(AvatarUiTextures.getBending, 0, 0, 70, 34);
		componentScrollSlots.setPadding(Measurement.fromPixels(7, 9));
		handler.add(componentScrollSlots);
		
		componentHotbar = new ComponentInventorySlots(container, 9, 1, container.getHotbarIndex(),
				container.getHotbarIndex() + 8);
		componentHotbar.setPosition(StartingPosition.MIDDLE_BOTTOM);
		componentHotbar.setOffset(Measurement.fromPixels(0, -7 * scaleFactor()));
		handler.add(componentHotbar);
		
		componentInventory = new ComponentInventorySlots(container, 9, 3, container.getInvIndex(),
				container.getInvIndex() + 26);
		componentInventory.setPosition(StartingPosition.MIDDLE_BOTTOM);
		componentInventory.useTexture(AvatarUiTextures.getBending, 0, 34, 176, 90);
		componentInventory.setPadding(Measurement.fromPixels(7, 7));
		handler.add(componentInventory);
		
	}
	
	@Override
	public void initGui() {
		super.initGui();
		ScreenInfo.refreshDimensions();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		handler.draw(partialTicks, mouseX, mouseY);
	}
	
}
