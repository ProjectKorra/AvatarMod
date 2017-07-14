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

import net.minecraft.client.gui.FontRenderer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ComponentText extends UiComponent {
	
	private final FontRenderer fontRender;
	private String text;
	
	public ComponentText(String text) {
		this.fontRender = mc.fontRenderer;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	protected float componentWidth() {
		return fontRender.getStringWidth(text);
	}
	
	@Override
	protected float componentHeight() {
		return fontRender.FONT_HEIGHT;
	}
	
	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {
		drawString(fontRender, text, 0, 0, 0xffffff);
	}
	
}
