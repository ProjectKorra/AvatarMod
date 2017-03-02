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
import com.crowsofwar.avatar.client.uitools.ComponentImage;
import com.crowsofwar.avatar.client.uitools.ComponentOverlay;
import com.crowsofwar.avatar.client.uitools.ComponentText;
import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.StartingPosition;
import com.crowsofwar.avatar.client.uitools.UiComponent;
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
	private Frame frame;
	private UiComponent icon, title, overlay, level, scrollBg, invBg;
	
	public WindowAbility(BendingAbility ability) {
		this.ability = ability;
		
		frame = new Frame();
		frame.setDimensions(fromPercent(80, 80));
		frame.setPosition(fromPercent((100 - 80) / 2, (100 - 80) / 2));
		
		title = new ComponentText(TextFormatting.BOLD + I18n.format("avatar.ability." + ability.getName()));
		title.setFrame(frame);
		title.setPosition(StartingPosition.MIDDLE_TOP);
		title.setScale(1.4f);
		
		icon = new ComponentImage(getAbilityTexture(ability), 0, 0, 256, 256);
		icon.setFrame(frame);
		icon.setPosition(StartingPosition.MIDDLE_TOP);
		icon.setOffset(fromPixels(0, title.height()).plus(fromPercent(0, -35)));
		
		level = new ComponentAbilityIcon(ability);
		level.setFrame(frame);
		level.setPosition(StartingPosition.TOP_RIGHT);
		
		scrollBg = new ComponentImage(AvatarUiTextures.skillsGui, 40, 0, 18, 18);
		scrollBg.setPosition(StartingPosition.MIDDLE_CENTER);
		// Not setting frame since should be absolutely positioned
		
		invBg = new ComponentImage(AvatarUiTextures.skillsGui, 0, 54, 169, 83);
		invBg.setPosition(StartingPosition.BOTTOM_RIGHT);
		// Not setting frame since should be absolutely positioned
		
		overlay = new ComponentOverlay();
		
	}
	
	public void draw(float partialTicks) {
		
		overlay.draw(partialTicks);
		frame.draw(partialTicks);
		title.draw(partialTicks);
		icon.draw(partialTicks);
		level.draw(partialTicks);
		scrollBg.draw(partialTicks);
		invBg.draw(partialTicks);
		
	}
	
	public boolean isMouseHover(float mouseX, float mouseY) {
		Measurement min = frame.getCoordsMin();
		Measurement max = frame.getCoordsMax();
		return mouseX > min.xInPixels() && mouseY > min.yInPixels() && mouseX < max.xInPixels()
				&& mouseY < max.yInPixels();
	}
	
}
