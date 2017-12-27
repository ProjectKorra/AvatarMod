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

import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableBlend;

/**
 * Like ComponentImage, but used for images that aren't square in dimension - like the background
 * images, which can be 1920x1080.
 *
 * @author CrowsOfWar
 */
public class ComponentImageNonSquare extends UiComponent {

	private final ResourceLocation texture;
	private final int texWidth, texHeight;

	public ComponentImageNonSquare(ResourceLocation texture, int textureWidth, int textureHeight) {
		this.texture = texture;
		this.texWidth = textureWidth;
		this.texHeight = textureHeight;
	}

	@Override
	protected float componentWidth() {
		return texWidth;
	}

	@Override
	protected float componentHeight() {
		return texHeight;
	}

	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {
		enableBlend();
		mc.renderEngine.bindTexture(texture);
		drawModalRectWithCustomSizedTexture(0, 0, 0, 0, texWidth, texHeight, texWidth, texHeight);
		disableBlend();
	}

}
