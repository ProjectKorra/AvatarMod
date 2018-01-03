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

import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.*;
import com.crowsofwar.avatar.common.bending.Ability;
import net.minecraft.client.renderer.GlStateManager;

import static com.crowsofwar.avatar.client.gui.AvatarUiTextures.getAbilityTexture;
import static com.crowsofwar.avatar.client.uitools.Measurement.fromPixels;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.scaleFactor;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenHeight;

/**
 * @author CrowsOfWar
 */
public class AbilityCard {

	private final Ability ability;
	private final int index;
	private Frame frame;
	private UiComponent icon, iconBg;

	public AbilityCard(Ability ability, int index) {

		this.ability = ability;
		this.index = index;

		// Init cards and icons

		icon = new ComponentImage(getAbilityTexture(ability), 0, 0, 256, 256);
		icon.setZLevel(2);

		frame = new Frame();
		frame.setDimensions(fromPixels(256, 256).times(scaleFactor()));

		icon.setFrame(frame);

		iconBg = new ComponentImage(AvatarUiTextures.skillsGui, 200, 137, 51, 50);
		iconBg.setFrame(frame);
		iconBg.setPosition(StartingPosition.TOP_LEFT);
		iconBg.setScale(2.5f);
		iconBg.setOffset(Measurement.fromPixels(64, 64).times(scaleFactor()));

		updateFramePos(0);

	}

	public void draw(float partialTicks, float scroll, float mouseX, float mouseY) {

		updateFramePos(scroll);

		GlStateManager.enableBlend();

		iconBg.draw(partialTicks, mouseX, mouseY);
		icon.draw(partialTicks, mouseX, mouseY);

	}

	/**
	 * Width in px
	 */
	public float width() {
		return frame.getDimensions().xInPixels();
	}

	public Ability getAbility() {
		return ability;
	}

	public boolean isMouseHover(float mouseX, float mouseY, float scroll) {

		// Returns whether mouse within iconBg

		updateFramePos(scroll);
		Measurement min = iconBg.coordinates();
		Measurement max = min.plus(Measurement.fromPixels(iconBg.width(), iconBg.height()));
		return mouseX > min.xInPixels() && mouseY > min.yInPixels() && mouseX < max.xInPixels()
				&& mouseY < max.yInPixels();

	}

	private void updateFramePos(float scroll) {

		Measurement base = fromPixels(50, (screenHeight() - icon.height()) / 2);
		Measurement offset = fromPixels(scroll + index * iconBg.width() * 1.4f, 0);
		frame.setPosition(base.plus(offset));

	}

}
