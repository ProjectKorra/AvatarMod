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
 * 
 * 
 * @author CrowsOfWar
 */
public class ComponentImage extends UiComponent {
	
	private final ResourceLocation texture;
	private final int u, v, texWidth, texHeight;
	
	public ComponentImage(ResourceLocation texture, int u, int v, int textureWidth, int textureHeight) {
		this.texture = texture;
		this.u = u;
		this.v = v;
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
		drawModalRectWithCustomSizedTexture(0, 0, u, v, texWidth, texHeight, texWidth, texHeight);
		disableBlend();
	}
	
}
