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

import static com.crowsofwar.avatar.client.gui.AvatarUiTextures.getCardTexture;
import static com.crowsofwar.avatar.client.uitools.Measurement.fromPercent;
import static com.crowsofwar.avatar.client.uitools.Measurement.fromPixels;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenHeight;

import com.crowsofwar.avatar.client.uitools.ComponentImage;
import com.crowsofwar.avatar.client.uitools.Frame;
import com.crowsofwar.avatar.client.uitools.Measurement;
import com.crowsofwar.avatar.client.uitools.StartingPosition;
import com.crowsofwar.avatar.client.uitools.UiComponent;
import com.crowsofwar.avatar.common.bending.BendingAbility;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityCard {
	
	private final BendingAbility ability;
	private final int index;
	private Frame frame;
	private UiComponent icon;
	
	public AbilityCard(BendingAbility ability, int index) {
		
		fromPixels(0, 0);
		fromPercent(0, 0);
		
		this.ability = ability;
		this.index = index;
		
		float width = 256 / 256 * 0.4f * 100, height = 256f / 256 * 0.4f * 100;
		
		frame = new Frame();
		frame.setDimensions(fromPixels(192, 256));
		updateFramePos(0);
		
		icon = new ComponentImage(getCardTexture(ability), 0, 0, 256, 256);
		icon.setFrame(frame);
		icon.setPosition(StartingPosition.MIDDLE_TOP);
		// icon.setOffset(fromPixels(frame, 0, -text.height() - icon.height() *
		// 50 / 256));
		icon.setScale(0.5f);
		
	}
	
	public void draw(float partialTicks, float scroll) {
		
		updateFramePos(scroll);
		
		icon.draw(partialTicks);
		// frame.draw(partialTicks);
		
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
	
	public boolean isMouseHover(float mouseX, float mouseY, float scroll) {
		
		updateFramePos(scroll);
		Measurement min = frame.getCoordsMin();
		Measurement max = frame.getCoordsMax();
		return mouseX > min.xInPixels() && mouseY > min.yInPixels() && mouseX < max.xInPixels()
				&& mouseY < max.yInPixels();
		
	}
	
	private void updateFramePos(float scroll) {
		
		Measurement base = fromPixels(50, (screenHeight() - 256) / 2);
		Measurement offset = fromPixels(scroll + index * width() * 1.2f, 0);
		frame.setPosition(base.plus(offset));
		
	}
	
}
