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

import static com.crowsofwar.avatar.client.gui.AvatarUiTextures.getPlainCardTexture;
import static com.crowsofwar.avatar.client.uitools.Measurement.fromPercent;
import static com.crowsofwar.avatar.client.uitools.Measurement.fromPixels;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.*;

import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.ComponentCustomButton;
import com.crowsofwar.avatar.client.uitools.ComponentImage;
import com.crowsofwar.avatar.client.uitools.ComponentLongText;
import com.crowsofwar.avatar.client.uitools.ComponentOverlay;
import com.crowsofwar.avatar.client.uitools.ComponentText;
import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.StartingPosition;
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.client.uitools.UiComponentHandler;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketSUseScroll;
import com.crowsofwar.gorecore.chat.ChatMessage;
import com.crowsofwar.gorecore.chat.ChatSender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class WindowAbility {
	
	private static final ChatMessage MSG_UNLOCK_TEXT = ChatMessage.newChatMessage("avatar.ui.unlockDesc",
			"bending");
	
	private final BendingAbility ability;
	private final SkillsGui gui;
	private final UiComponentHandler handler;
	
	private Frame frame;
	private UiComponent icon, title, overlay, level, invBg, treeView, description, backButton;
	private ComponentInventorySlots slot1, slot2;
	private ComponentAbilityKeybind keybind;
	private ComponentCustomButton button;
	
	private UiComponent unlockTitle, unlockText;
	private ComponentCustomButton unlockButton;
	
	public WindowAbility(BendingAbility ability, SkillsGui gui) {
		this.ability = ability;
		this.gui = gui;
		this.handler = new UiComponentHandler();
		
		frame = new Frame();
		frame.setDimensions(fromPercent(80, 80));
		frame.setPosition(fromPercent((100 - 80) / 2, (100 - 80) / 2));
		
		Frame frameLeft = new Frame(frame);
		frameLeft.setDimensions(fromPercent(frame, 30, 100));
		
		Frame frameRight = new Frame(frame);
		frameRight.setDimensions(fromPercent(frame, 60, 100));
		frameRight.setPosition(fromPercent(frame, 40, 0));
		
		overlay = new ComponentOverlay();
		handler.add(overlay);
		
		title = new ComponentText(TextFormatting.BOLD + I18n.format("avatar.ability." + ability.getName()));
		title.setFrame(frameLeft);
		title.setPosition(StartingPosition.MIDDLE_TOP);
		title.setScale(1.4f);
		title.setZLevel(4);
		handler.add(title);
		
		icon = new ComponentImage(getPlainCardTexture(ability), 0, 0, 256, 256);
		icon.setFrame(frameLeft);
		icon.setPosition(StartingPosition.MIDDLE_TOP);
		icon.setOffset(fromPixels(0, title.height()).plus(fromPercent(0, -35)));
		icon.setZLevel(3);
		handler.add(icon);
		
		description = new ComponentLongText(I18n.format("avatar.ability." + ability.getName() + ".desc"),
				fromPercent(frameLeft, 100, 0));
		description.setFrame(frameLeft);
		description.setPosition(StartingPosition.custom(0, 0.2f, 0, 0));
		description.setZLevel(4);
		handler.add(description);
		
		// level = new ComponentAbilityIcon(ability);
		// level.setFrame(frameRight);
		// level.setPosition(StartingPosition.TOP_RIGHT);
		// handler.add(level);
		
		invBg = new ComponentImage(AvatarUiTextures.skillsGui, 0, 54, 169, 83);
		invBg.setPosition(StartingPosition.BOTTOM_RIGHT);
		// Not setting frame since should be absolutely positioned
		// Don't add invBg since it shouldn't be rendered
		// invBg is used to figure out where inventory is
		
		slot1 = new ComponentInventorySlots(gui.inventorySlots, 0);
		slot1.useTexture(AvatarUiTextures.skillsGui, 40, 0, 18, 18);
		slot1.setZLevel(5);
		handler.add(slot1);
		
		slot2 = new ComponentInventorySlots(gui.inventorySlots, 1);
		slot2.useTexture(AvatarUiTextures.skillsGui, 40, 0, 18, 18);
		slot2.setOffset(Measurement.fromPixels(frameRight, slot1.width() + 10, 0));
		slot2.setZLevel(5);
		handler.add(slot2);
		
		treeView = new ComponentAbilityTree(ability, slot1, slot2);
		treeView.setFrame(frameRight);
		treeView.setPosition(StartingPosition.TOP_LEFT);
		treeView.setOffset(Measurement.fromPercent(frameRight, 0, 20));
		treeView.setZLevel(4);
		handler.add(treeView);
		
		button = new ComponentCustomButton(AvatarUiTextures.skillsGui, 112, 0, 18, 18,
				() -> gui.useScroll(ability));
		button.setFrame(frameRight);
		button.setPosition(StartingPosition.TOP_LEFT);
		// button.setOffset(fromPixels(gui.getScrollSlot().width() * 1.5f, 0));
		button.setOffset(treeView.offset().plus(fromPixels(frameRight, treeView.width() + 100, 0)));
		button.setZLevel(4);
		handler.add(button);
		
		keybind = new ComponentAbilityKeybind(ability);
		keybind.setFrame(frameRight);
		keybind.setPosition(StartingPosition.custom(0.5f, 0.5f, 1, 0.5f));
		keybind.setOffset(Measurement.fromPercent(frameRight, -4, 0));
		keybind.setZLevel(4);
		handler.add(keybind);
		
		unlockTitle = new ComponentText(TextFormatting.BOLD + I18n.format("avatar.ui.unlock"));
		unlockTitle.setFrame(frameRight);
		unlockTitle.setScale(1.5f);
		unlockTitle.setZLevel(4);
		handler.add(unlockTitle);
		
		String bendingName = ability.getBendingType().name().toLowerCase();
		String text = ChatSender.instance.processText(I18n.format("avatar.ui.unlockDesc"), MSG_UNLOCK_TEXT,
				bendingName);
		unlockText = new ComponentLongText(text, frameRight.getDimensions());
		unlockText.setFrame(frameRight);
		unlockText.setZLevel(4);
		unlockText.setOffset(fromPixels(frameRight, 0, unlockTitle.height() + 10));
		handler.add(unlockText);
		
		unlockButton = new ComponentCustomButton(AvatarUiTextures.skillsGui, 196, 100, 20, 20,
				() -> AvatarMod.network.sendToServer(new PacketSUseScroll(ability)));
		unlockButton.setFrame(frameRight);
		unlockButton.setOffset(fromPixels(unlockTitle.getFrame(), slot1.width() + 20,
				unlockTitle.height() + unlockText.height() + 20));
		unlockButton.setZLevel(4);
		handler.add(unlockButton);
		
		backButton = new ComponentCustomButton(AvatarUiTextures.skillsGui, 0, 240, 16, 16,
				() -> gui.closeWindow());
		backButton.setZLevel(999);
		handler.add(backButton);
		
	}
	
	public void draw(float partialTicks) {
		
		button.setEnabled(
				gui.inventorySlots.getSlot(0).getHasStack() || gui.inventorySlots.getSlot(1).getHasStack());
		
		Minecraft mc = Minecraft.getMinecraft();
		int width = screenWidth() / scaleFactor();
		int height = screenHeight() / scaleFactor();
		int mouseX = Mouse.getX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;
		
		AbilityData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer).getAbilityData(ability);
		
		unlockTitle.setVisible(data.isLocked());
		unlockText.setVisible(data.isLocked());
		unlockButton.setVisible(data.isLocked());
		
		treeView.setVisible(!data.isLocked());
		button.setVisible(!data.isLocked());
		
		if (data.isLocked()) {
			unlockButton.setEnabled(gui.inventorySlots.getSlot(0).getHasStack());
			slot1.setVisible(true);
			slot1.setFrame(unlockTitle.getFrame());
			slot1.setOffset(
					fromPixels(unlockTitle.getFrame(), 0, unlockTitle.height() + unlockText.height() + 20));
			slot2.setVisible(false);
		} else {
			slot1.setFrame(Frame.SCREEN);
		}
		
		handler.draw(partialTicks, mouseX, mouseY);
		
	}
	
	public boolean isMouseHover(float mouseX, float mouseY) {
		Measurement min = frame.getCoordsMin();
		Measurement max = frame.getCoordsMax();
		return mouseX > min.xInPixels() && mouseY > min.yInPixels() && mouseX < max.xInPixels()
				&& mouseY < max.yInPixels();
	}
	
	public boolean isInventoryMouseHover(float mouseX, float mouseY) {
		Measurement min = invBg.coordinates();
		Measurement max = min.plus(fromPixels(invBg.width(), invBg.height()));
		return mouseX > min.xInPixels() && mouseY > min.yInPixels() && mouseX < max.xInPixels()
				&& mouseY < max.yInPixels();
	}
	
	public void mouseClicked(float x, float y, int button) {
		handler.click(x, y, button);
	}
	
	public void keyTyped(int key) {
		handler.type(key);
	}
	
	public Frame getFrame() {
		return frame;
	}
	
	public boolean isEditing() {
		return keybind.isEditing();
	}
	
	public void onClose() {
		slot1.setVisible(false);
		slot2.setVisible(false);
		// Make slots update their position & disappear
		slot1.draw(0, 0, 0);
		slot2.draw(0, 0, 0);
	}
	
}
