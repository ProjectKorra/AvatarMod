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

import static com.crowsofwar.avatar.client.uitools.Measurement.fromPixels;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenHeight;

import org.lwjgl.input.Mouse;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

/**
 * Allows similar functionality to GuiButton, but with a custom texture.
 * 
 * @author CrowsOfWar
 */
public class ComponentCustomButton extends UiComponent {
	
	private final ResourceLocation texture;
	private final int startU, startV, width, height;
	private final Runnable onClick;
	
	private boolean enabled, wasDown;
	
	/**
	 * Create a custom button. U and V specified should be initial U/V for
	 * regular (nohover) button. Next to that on the texture, there should be a
	 * hover version of the button, then a disabled version of the button.
	 */
	public ComponentCustomButton(ResourceLocation texture, int u, int v, int width, int height,
			Runnable onClick) {
		this.texture = texture;
		this.startU = u;
		this.startV = v;
		this.width = width;
		this.height = height;
		this.onClick = onClick;
		
		this.enabled = true;
	}
	
	@Override
	protected float componentWidth() {
		return width;
	}
	
	@Override
	protected float componentHeight() {
		return height;
	}
	
	@Override
	protected void componentDraw(float partialTicks) {
		
		mc.renderEngine.bindTexture(texture);
		
		int mouseX = Mouse.getX(), mouseY = screenHeight() - Mouse.getY();
		
		Measurement min = coordinates();
		Measurement max = min.plus(fromPixels(width(), height()));
		boolean hover = mouseX > min.xInPixels() && mouseY > min.yInPixels() && mouseX < max.xInPixels()
				&& mouseY < max.yInPixels();
		
		int u = startU;
		if (!enabled) {
			u += width * 2;
		} else if (hover) {
			u += width;
		}
		
		drawTexturedModalRect(0, 0, u, startV, width, height);
		
		boolean down = Mouse.isButtonDown(0);
		if (enabled && down && !wasDown && hover) {
			onClick.run();
			mc.getSoundHandler()
					.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
		wasDown = down;
		
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
