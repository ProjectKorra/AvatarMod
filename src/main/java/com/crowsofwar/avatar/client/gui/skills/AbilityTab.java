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

import com.crowsofwar.avatar.client.uitools.ComponentImage;
import com.crowsofwar.avatar.client.uitools.ComponentText;
import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.StartingPosition;
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.BendingAbility;

import net.minecraft.client.resources.I18n;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityTab {
	
	private final BendingAbility ability;
	private Frame frame;
	private UiComponent icon, text;
	
	public AbilityTab(BendingAbility ability) {
		
		fromPixels(0, 0);
		fromPercent(0, 0);
		
		this.ability = ability;
		
		float width = 25f, height = 100 * 2 / 3f;
		
		frame = new Frame();
		frame.setDimensions(fromPercent(width, height));
		frame.setPosition(fromPercent((100 - width) / 2, (100 - height) / 2));
		
		text = new ComponentText(I18n.format("avatar.ability." + ability.getName()));
		text.setFrame(frame);
		text.setPosition(StartingPosition.MIDDLE_TOP);
		
		icon = new ComponentImage(getAbilityTexture(ability), 0, 0, 256, 256);
		icon.setFrame(frame);
		icon.setPosition(StartingPosition.MIDDLE_TOP);
		icon.setOffset(fromPixels(frame, 0, -text.height() - icon.height() * 50 / 256));
		
	}
	
	public void draw(float partialTicks, float scroll) {
		
		float width = 25f, height = 100 * 2 / 3f;
		Measurement base = fromPercent((100 - width) / 2, (100 - height) / 2);
		frame.setPosition(base.plus(fromPixels(scroll, 0)));
		
		frame.draw(partialTicks);
		icon.draw(partialTicks);
		text.draw(partialTicks);
		
	}
	
	/**
	 * Width in px
	 */
	public float width() {
		return frame.getDimensions().xInPixels();
	}
	
	public BendingAbility getAbility() {
		return ability;
	}
	
}
