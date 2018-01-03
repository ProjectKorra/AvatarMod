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
package com.crowsofwar.avatar.client.uitools;

import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import net.minecraft.client.renderer.GlStateManager;

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenHeight;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenWidth;

/**
 * UI component which covers the whole screen in a background shade
 *
 * @author CrowsOfWar
 */
public class ComponentOverlay extends UiComponent {

	@Override
	protected float componentWidth() {
		return screenWidth();
	}

	@Override
	protected float componentHeight() {
		return screenHeight();
	}

	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {

		mc.renderEngine.bindTexture(AvatarUiTextures.WHITE);
		GlStateManager.enableBlend();
		GlStateManager.color(0, 0, 0, .5f);
		drawTexturedModalRect(0, 0, 0, 0, screenWidth(), screenHeight());

		GlStateManager.disableBlend();
	}

}
