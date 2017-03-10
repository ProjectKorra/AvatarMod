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

import static com.crowsofwar.avatar.client.gui.AvatarUiTextures.getAbilityTexture;
import static com.crowsofwar.avatar.client.uitools.Measurement.fromPercent;
import static com.crowsofwar.avatar.client.uitools.Measurement.fromPixels;

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

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class WindowAbility {
	
	private final BendingAbility ability;
	private final GuiSkillsNew gui;
	private final UiComponentHandler handler;
	
	private Frame frame;
	private UiComponent icon, title, overlay, level, invBg, treeView, description;
	private ComponentInventorySlots slot1, slot2;
	private ComponentAbilityKeybind keybind;
	private ComponentCustomButton button;
	
	public WindowAbility(BendingAbility ability, GuiSkillsNew gui) {
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
		handler.add(title);
		
		icon = new ComponentImage(getAbilityTexture(ability), 0, 0, 256, 256);
		icon.setFrame(frameLeft);
		icon.setPosition(StartingPosition.MIDDLE_TOP);
		icon.setOffset(fromPixels(0, title.height()).plus(fromPercent(0, -35)));
		handler.add(icon);
		
		description = new ComponentLongText(I18n.format("avatar.ability." + ability.getName() + ".desc"),
				fromPercent(frameLeft, 100, 0));
		description.setFrame(frameLeft);
		description.setPosition(StartingPosition.custom(0.5f, 0.5f, 0.5f, -1f));
		handler.add(description);
		
		level = new ComponentAbilityIcon(ability);
		level.setFrame(frameRight);
		level.setPosition(StartingPosition.TOP_RIGHT);
		handler.add(level);
		
		invBg = new ComponentImage(AvatarUiTextures.skillsGui, 0, 54, 169, 83);
		invBg.setPosition(StartingPosition.BOTTOM_RIGHT);
		// Not setting frame since should be absolutely positioned
		// Don't add invBg since it shouldn't be rendered
		
		slot1 = new ComponentInventorySlots(gui.inventorySlots, 0);
		slot1.useTexture(AvatarUiTextures.skillsGui, 40, 0, 18, 18);
		slot2 = new ComponentInventorySlots(gui.inventorySlots, 0);
		slot2.useTexture(AvatarUiTextures.skillsGui, 40, 0, 18, 18);
		slot2.setOffset(Measurement.fromPixels(frameRight, slot1.width() + 10, 0));
		// Add slots later so on top of treeView
		
		treeView = new ComponentAbilityTree(ability, slot1, slot2);
		treeView.setFrame(frameRight);
		treeView.setPosition(StartingPosition.TOP_LEFT);
		treeView.setOffset(Measurement.fromPercent(frameRight, 0, 20));
		handler.add(treeView);
		
		handler.add(slot1);
		handler.add(slot2);
		
		button = new ComponentCustomButton(AvatarUiTextures.skillsGui, 112, 0, 18, 18,
				() -> gui.useScroll(ability));
		button.setFrame(frameRight);
		button.setPosition(StartingPosition.TOP_LEFT);
		// button.setOffset(fromPixels(gui.getScrollSlot().width() * 1.5f, 0));
		button.setOffset(treeView.offset().plus(fromPixels(frameRight, treeView.width() + 100, 0)));
		handler.add(button);
		
		keybind = new ComponentAbilityKeybind(ability);
		keybind.setFrame(frameRight);
		keybind.setPosition(StartingPosition.custom(0.5f, 0.5f, 1, 0.5f));
		keybind.setOffset(Measurement.fromPercent(frameRight, -4, 0));
		handler.add(keybind);
		
	}
	
	public void draw(float partialTicks) {
		
		button.setEnabled(gui.inventorySlots.getSlot(0).getHasStack());
		
		handler.draw(partialTicks);
		
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
	
}
