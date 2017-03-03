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

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.*;
import static com.crowsofwar.avatar.common.bending.BendingAbility.*;
import static net.minecraft.client.Minecraft.getMinecraft;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.ScreenInfo;
import com.crowsofwar.avatar.client.uitools.StartingPosition;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.ContainerSkillsGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class GuiSkillsNew extends GuiContainer implements AvatarGui {
	
	private AbilityTab[] tabs;
	private int scroll;
	
	private WindowAbility window;
	private Frame frame;
	
	private ComponentInventorySlots scrollSlot, inventory, hotbar;
	
	public GuiSkillsNew() {
		super(new ContainerSkillsGui(getMinecraft().thePlayer, screenWidth() / scaleFactor(),
				screenHeight() / scaleFactor()));
		
		ContainerSkillsGui skillsContainer = (ContainerSkillsGui) inventorySlots;
		
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		
		xSize = res.getScaledWidth();
		ySize = res.getScaledHeight();
		
		ScreenInfo.refreshDimensions();
		
		tabs = new AbilityTab[] { new AbilityTab(ABILITY_AIR_BUBBLE), new AbilityTab(ABILITY_AIR_GUST),
				new AbilityTab(ABILITY_AIR_JUMP), new AbilityTab(ABILITY_AIRBLADE) };
		
		scrollSlot = new ComponentInventorySlots(inventorySlots, 0);
		scrollSlot.useTexture(AvatarUiTextures.skillsGui, 40, 0, 18, 18);
		scrollSlot.setPosition(StartingPosition.MIDDLE_CENTER);
		scrollSlot.setVisible(false);
		
		inventory = new ComponentInventorySlots(inventorySlots, 9, 3, skillsContainer.getInvIndex(),
				skillsContainer.getInvIndex() + 26);
		inventory.useTexture(AvatarUiTextures.skillsGui, 0, 54, 169, 83);
		inventory.setPosition(StartingPosition.BOTTOM_RIGHT);
		inventory.setVisible(false);
		
		hotbar = new ComponentInventorySlots(inventorySlots, 9, 0, skillsContainer.getHotbarIndex(),
				skillsContainer.getHotbarIndex() + 8);
		hotbar.setPosition(StartingPosition.BOTTOM_RIGHT);
		hotbar.setVisible(false);
		
	}
	
	@Override
	public void initGui() {
		super.initGui();
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		
		ScreenInfo.refreshDimensions();
		
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		
		ContainerSkillsGui container = (ContainerSkillsGui) inventorySlots;
		ItemStack scroll = container.getSlot(0).getStack();
		
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		scroll += Mouse.getDWheel() / 3;
		
		if (Mouse.isButtonDown(0)) {
			
			int mouseX = Mouse.getX(), mouseY = screenHeight() - Mouse.getY();
			
			if (isWindowOpen()) {
				if (!window.isMouseHover(mouseX, mouseY) && !window.isInventoryMouseHover(mouseX, mouseY)) {
					closeWindow();
				}
			} else {
				for (int i = 0; i < tabs.length; i++) {
					if (tabs[i].isMouseHover(mouseX, mouseY, scroll + 1.2f * i * tabs[i].width())) {
						openWindow(tabs[i]);
						break;
					}
				}
			}
			
		}
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer);
		
		for (int i = 0; i < tabs.length; i++) {
			tabs[i].draw(partialTicks, scroll + 1.2f * i * tabs[i].width());
		}
		
		if (isWindowOpen()) {
			window.draw(partialTicks);
		}
		
		scrollSlot.draw(partialTicks);
		inventory.draw(partialTicks);
		hotbar.draw(partialTicks);
		
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (isWindowOpen()
				&& (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))) {
			closeWindow();
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}
	
	private boolean isWindowOpen() {
		return window != null;
	}
	
	private void openWindow(AbilityTab tab) {
		window = new WindowAbility(tab.getAbility());
		scrollSlot.setVisible(true);
		inventory.setVisible(true);
		hotbar.setVisible(true);
		scrollSlot.setFrame(window.getFrame());
	}
	
	private void closeWindow() {
		window = null;
		scrollSlot.setVisible(false);
		inventory.setVisible(false);
		hotbar.setVisible(false);
		scrollSlot.setFrame(Frame.SCREEN);
	}
	
}
