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
import static net.minecraft.client.Minecraft.getMinecraft;
import static org.lwjgl.input.Keyboard.KEY_ESCAPE;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.ComponentText;
import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.ScreenInfo;
import com.crowsofwar.avatar.client.uitools.StartingPosition;
import com.crowsofwar.avatar.client.uitools.UiComponentHandler;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.ContainerSkillsGui;
import com.crowsofwar.avatar.common.network.packets.PacketSUseScroll;
import com.crowsofwar.gorecore.format.FormattedMessage;
import com.crowsofwar.gorecore.format.FormattedMessageProcessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SkillsGui extends GuiContainer implements AvatarGui {
	
	private static final FormattedMessage MSG_TITLE = FormattedMessage.newChatMessage("avatar.ui.skillsMenu",
			"bending");
	
	private AbilityCard[] cards;
	private ComponentBendingTab[] tabs;
	private int scroll;
	
	private WindowAbility window;
	private Frame frame;
	
	private ComponentInventorySlots inventory, hotbar;
	private ComponentText title;
	private UiComponentHandler handler;
	
	public SkillsGui(BendingType type) {
		super(new ContainerSkillsGui(getMinecraft().thePlayer, type));
		
		ContainerSkillsGui skillsContainer = (ContainerSkillsGui) inventorySlots;
		BendingData data = AvatarPlayerData.fetcher().fetch(getMinecraft().thePlayer);
		
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		
		xSize = res.getScaledWidth();
		ySize = res.getScaledHeight();
		
		ScreenInfo.refreshDimensions();
		
		BendingController controller = BendingManager.getBending(type);
		List<BendingAbility> abilities = controller.getAllAbilities();
		cards = new AbilityCard[abilities.size()];
		for (int i = 0; i < abilities.size(); i++) {
			cards[i] = new AbilityCard(abilities.get(i), i);
		}
		
		handler = new UiComponentHandler();
		
		BendingType[] types = data.getAllBending().stream()//
				.map(c -> c.getType())//
				.sorted((c1, c2) -> c1.name().compareTo(c2.name()))//
				.toArray(BendingType[]::new);
		
		tabs = new ComponentBendingTab[types.length];
		for (int i = 0; i < types.length; i++) {
			float scale = 1.4f;
			tabs[i] = new ComponentBendingTab(types[i], types[i] == type);
			tabs[i].setPosition(StartingPosition.MIDDLE_BOTTOM);
			tabs[i].setOffset(Measurement.fromPixels(24 * scaleFactor() * (i - types.length / 2) * scale, 0));
			tabs[i].setScale(scale);
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
		
		title = new ComponentText(TextFormatting.BOLD + FormattedMessageProcessor.formatText(MSG_TITLE,
				I18n.format("avatar.ui.skillsMenu"), type.name().toLowerCase()));
		title.setPosition(StartingPosition.TOP_CENTER);
		title.setOffset(Measurement.fromPixels(0, 10));
		handler.add(title);
		
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
		
		if (Mouse.isButtonDown(0) && !isWindowOpen()) {
			
			int mouseX = Mouse.getX(), mouseY = screenHeight() - Mouse.getY();
			
			for (int i = 0; i < cards.length; i++) {
				if (cards[i].isMouseHover(mouseX, mouseY, scroll)) {
					openWindow(cards[i]);
					break;
				}
			}
			
		}
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer);
		
		handler.draw(partialTicks, mouseX, mouseY);
		
		if (isWindowOpen()) {
			window.draw(partialTicks);
		} else {
			
			for (int i = 0; i < cards.length; i++) {
				cards[i].draw(partialTicks, scroll, mouseX, mouseY);
			}
			
		}
		
		inventory.setVisible(isWindowOpen());
		inventory.draw(partialTicks, mouseX, mouseY);
		hotbar.draw(partialTicks, mouseX, mouseY);
		
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		if (!isWindowOpen()) {
			handler.type(keyCode);
		}
		
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
		if (window != null) {
			window.mouseClicked(mouseX, mouseY, mouseButton);
		} else {
			handler.click(mouseX, mouseY, mouseButton);
		}
	}
	
	@Override
	protected void renderToolTip(ItemStack stack, int x, int y) {
		// Prevent rendering tooltip of ItemStacks if they are in a slot that
		// needs a tooltip
		if (!isWindowOpen() || window.canRenderTooltip(stack)) {
			super.renderToolTip(stack, x, y);
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
	
	public void closeWindow() {
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
