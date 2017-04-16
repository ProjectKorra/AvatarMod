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

import static com.crowsofwar.avatar.client.uitools.Measurement.fromPixels;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.scaleFactor;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenHeight;
import static com.crowsofwar.avatar.common.bending.BendingAbility.*;
import static net.minecraft.client.Minecraft.getMinecraft;
import static org.lwjgl.input.Keyboard.KEY_ESCAPE;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.ScreenInfo;
import com.crowsofwar.avatar.client.uitools.StartingPosition;
import com.crowsofwar.avatar.client.uitools.UiComponentHandler;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.ContainerSkillsGui;
import com.crowsofwar.avatar.common.network.packets.PacketSUseScroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SkillsGui extends GuiContainer implements AvatarGui {
	
	private AbilityCard[] cards;
	private ComponentBendingTab[] tabs;
	private int scroll;
	
	private WindowAbility window;
	private Frame frame;
	
	private ComponentInventorySlots inventory, hotbar;
	private UiComponentHandler handler;
	
	public SkillsGui() {
		super(new ContainerSkillsGui(getMinecraft().thePlayer, BendingType.AIRBENDING));
		
		ContainerSkillsGui skillsContainer = (ContainerSkillsGui) inventorySlots;
		
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		
		xSize = res.getScaledWidth();
		ySize = res.getScaledHeight();
		
		ScreenInfo.refreshDimensions();
		
		cards = new AbilityCard[] { new AbilityCard(ABILITY_AIR_BUBBLE, 0),
				new AbilityCard(ABILITY_AIR_GUST, 1), new AbilityCard(ABILITY_AIR_JUMP, 2),
				new AbilityCard(ABILITY_AIRBLADE, 3) };
		
		handler = new UiComponentHandler();
		BendingType[] types = BendingType.values();
		tabs = new ComponentBendingTab[types.length];
		for (int i = 1; i < types.length; i++) {
			tabs[i] = new ComponentBendingTab(types[i], false);
			tabs[i].setPosition(StartingPosition.MIDDLE_BOTTOM);
			tabs[i].setOffset(Measurement.fromPixels(24 * scaleFactor() * (i - types.length / 2), 0));
			handler.add(tabs[i]);
		}
		
		inventory = new ComponentInventorySlots(inventorySlots, 9, 3, skillsContainer.getInvIndex(),
				skillsContainer.getInvIndex() + 26);
		inventory.useTexture(AvatarUiTextures.skillsGui, 0, 54, 169, 83);
		inventory.setPosition(StartingPosition.BOTTOM_RIGHT);
		inventory.setPadding(fromPixels(7, 7));
		inventory.setVisible(false);
		
		hotbar = new ComponentInventorySlots(inventorySlots, 9, 1, skillsContainer.getHotbarIndex(),
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
				for (int i = 0; i < cards.length; i++) {
					if (cards[i].isMouseHover(mouseX, mouseY, scroll)) {
						openWindow(cards[i]);
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
		
		for (int i = 0; i < cards.length; i++) {
			cards[i].draw(partialTicks, scroll);
		}
		
		if (isWindowOpen()) {
			window.draw(partialTicks);
		}
		
		inventory.draw(partialTicks);
		hotbar.draw(partialTicks);
		handler.draw(partialTicks, mouseX, mouseY);
		
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		handler.type(keyCode);
		
		if (isWindowOpen()) {
			KeyBinding invKb = mc.gameSettings.keyBindInventory;
			
			if (window.isEditing() && keyCode == KEY_ESCAPE) {
				window.keyTyped(keyCode);
			} else if (keyCode == 1 || invKb.isActiveAndMatches(keyCode)) {
				closeWindow();
			} else {
				window.keyTyped(keyCode);
			}
			
		} else {
			
			if (keyCode == Keyboard.KEY_A || keyCode == Keyboard.KEY_LEFT) {
				scroll += 25;
			} else if (keyCode == Keyboard.KEY_D || keyCode == Keyboard.KEY_RIGHT) {
				scroll -= 25;
			} else {
				super.keyTyped(typedChar, keyCode);
			}
			
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		handler.click(mouseX, mouseY, mouseButton);
		if (window != null) {
			window.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	
	private boolean isWindowOpen() {
		return window != null;
	}
	
	private void openWindow(AbilityCard card) {
		window = new WindowAbility(card.getAbility(), this);
		inventory.setVisible(true);
		hotbar.setVisible(true);
	}
	
	private void closeWindow() {
		window.onClose();
		window = null;
		inventory.setVisible(false);
		hotbar.setVisible(false);
		
	}
	
	/**
	 * Called when the 'use scroll' button is clicked
	 */
	public void useScroll(BendingAbility ability) {
		ContainerSkillsGui container = (ContainerSkillsGui) inventorySlots;
		
		if (container.getSlot(0).getHasStack() || container.getSlot(1).getHasStack()) {
			AvatarMod.network.sendToServer(new PacketSUseScroll(ability));
		}
		
	}
	
}
