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
	private UiComponent icon, title, overlay, level, invBg, treeView;
	private ComponentAbilityKeybind keybind;
	private ComponentCustomButton button;
	
	public WindowAbility(BendingAbility ability, GuiSkillsNew gui) {
		this.ability = ability;
		this.gui = gui;
		this.handler = new UiComponentHandler();
		
		frame = new Frame();
		frame.setDimensions(fromPercent(80, 80));
		frame.setPosition(fromPercent((100 - 80) / 2, (100 - 80) / 2));
		
		overlay = new ComponentOverlay();
		handler.add(overlay);
		
		title = new ComponentText(TextFormatting.BOLD + I18n.format("avatar.ability." + ability.getName()));
		title.setFrame(frame);
		title.setPosition(StartingPosition.MIDDLE_TOP);
		title.setScale(1.4f);
		handler.add(title);
		
		icon = new ComponentImage(getAbilityTexture(ability), 0, 0, 256, 256);
		icon.setFrame(frame);
		icon.setPosition(StartingPosition.MIDDLE_TOP);
		icon.setOffset(fromPixels(0, title.height()).plus(fromPercent(0, -35)));
		handler.add(icon);
		
		level = new ComponentAbilityIcon(ability);
		level.setFrame(frame);
		level.setPosition(StartingPosition.TOP_RIGHT);
		handler.add(level);
		
		invBg = new ComponentImage(AvatarUiTextures.skillsGui, 0, 54, 169, 83);
		invBg.setPosition(StartingPosition.BOTTOM_RIGHT);
		// Not setting frame since should be absolutely positioned
		// Don't add invBg since it shouldn't be rendered
		
		treeView = new ComponentAbilityTree(ability);
		treeView.setFrame(frame);
		treeView.setPosition(StartingPosition.MIDDLE_BOTTOM);
		treeView.setOffset(Measurement.fromPercent(frame, 0, -30));
		handler.add(treeView);
		
		button = new ComponentCustomButton(AvatarUiTextures.skillsGui, 112, 0, 18, 18,
				() -> gui.useScroll(ability));
		button.setFrame(frame);
		button.setPosition(StartingPosition.MIDDLE_CENTER);
		button.setOffset(fromPixels(gui.getScrollSlot().width() * 1.5f, 0));
		handler.add(button);
		
		keybind = new ComponentAbilityKeybind(ability);
		keybind.setFrame(frame);
		keybind.setPosition(StartingPosition.custom(0.5f, 0.5f, 1, 0.5f));
		keybind.setOffset(Measurement.fromPercent(-4, 0));
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
	
	public Frame getFrame() {
		return frame;
	}
	
}
