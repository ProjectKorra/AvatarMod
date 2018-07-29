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

package com.crowsofwar.avatar.client.gui;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import static com.crowsofwar.avatar.client.gui.RadialMenu.*;
import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * Holds information for the RadialMenu about a segment. Contains information on
 * its rotation (position), and whether it's clicked.
 */
public class RadialSegment extends Gui {

	private final RadialMenu gui;
	private final MenuTheme theme;
	private final Minecraft mc;
	private final float angle;
	private final int index;
	private final Ability ability;
	private final BendingStyle element;

	public RadialSegment(RadialMenu gui, MenuTheme theme, int index, Ability ability, BendingStyle element) {
		this.gui = gui;
		this.angle = 22.5F + index * 45;
		this.index = index;
		this.ability = ability;
		this.theme = theme;
		this.mc = Minecraft.getMinecraft();
		this.element = element;

	}

	/**
	 * Returns whether the mouse is currently hovering
	 *
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public boolean isMouseHover(int mouseX, int mouseY, ScaledResolution resolution) {

		int mouseCenteredX = mouseX - resolution.getScaledWidth() / 2;
		int mouseCenteredY = mouseY - resolution.getScaledHeight() / 2;
		double r = Math.sqrt(mouseCenteredX * mouseCenteredX + mouseCenteredY * mouseCenteredY)
				/ RadialMenu.menuScale;
		double currentAngle = Math.toDegrees(Math.atan2(mouseCenteredY, mouseCenteredX)) + 90;
		double minAngle = angle - 45;
		if (minAngle < 0) minAngle += 360;
		double maxAngle = angle;
		boolean addCurrentAngle = currentAngle < 0;
		if (minAngle > maxAngle) {
			maxAngle += 360;
			addCurrentAngle = true;
		}
		if (addCurrentAngle) currentAngle += 360;

		return r >= 100 && r <= 300 && currentAngle >= minAngle && currentAngle <= maxAngle;
	}

	public float getAngle() {
		return angle;
	}

	/**
	 * Draw this radial segment.
	 *
	 * @param hover
	 *            Whether mouse is over it
	 * @param resolution
	 *            Resolution MC is at
	 * @param alpha
	 *            Alpha of the image; 0 for completely transparent and 1 for
	 *            completely opaque
	 * @param scale
	 *            Scale of the image, 1 for no change
	 */
	//@formatter:off
	public void draw(boolean hover, ScaledResolution resolution, float alpha, float scale) {
		
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();
		
		GlStateManager.enableBlend();
		
		// Draw background & edge
		GlStateManager.pushMatrix();
			GlStateManager.translate(width / 2f, height / 2f, 0); 	// Re-center origin
			GlStateManager.scale(menuScale, menuScale, menuScale); 	// Scale all following arguments
			GlStateManager.scale(.9f, .9f, 1);
			GlStateManager.rotate(this.getAngle(), 0, 0, 1);		// All transform operations and the image are rotated
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(-segmentX, -segmentY, 0);		// Offset the image to the correct
																	// center point
			// Draw background
			GlStateManager.color(theme.getBackground().getRed(hover) / 255f,
					theme.getBackground().getGreen(hover) / 255f, theme.getBackground().getBlue(hover) / 255f, alpha);
			mc.getTextureManager().bindTexture(AvatarUiTextures.getBendingRadialTexture(element));
			drawTexturedModalRect(0, 0, 0, 0, 256, 256);
			
		GlStateManager.popMatrix();
		
		// Draw icon
		GlStateManager.pushMatrix();
			float iconScale = .4f;
			float angle = this.getAngle() - 20f;
			angle %= 360;
			
			// Recenter over origin
			translate((width - 256 * iconScale) / 2f, (height - 256 * iconScale) / 2f, 0);
			// Translate to the correct position
			rotate(angle, 0, 0, 1);
			translate(0, -200 * .9f * menuScale * scale, 0);
			rotate(-angle, 0, 0, 1);
			// Last transform before draw
			scale(iconScale, iconScale, 1);
			
			// Ensure icon is not overlapped
			GlStateManager.translate(0, 0, 2);
			
			if (ability != null) {
				mc.getTextureManager().bindTexture(AvatarUiTextures.getAbilityTexture(ability));
				drawTexturedModalRect(0, 0, 0, 0, 256, 256);
			}
			
//			float darkenBy = 0.05f;
//			float r = theme.getIcon().getRed(hover) / 255f - darkenBy;
//			float g = theme.getIcon().getGreen(hover) / 255f - darkenBy;
//			float b = theme.getIcon().getBlue(hover) / 255f - darkenBy;
//			float avg = (r + g + b) / 3;
//			GlStateManager.color(avg, avg, avg, alpha);
			
			// TODO Blurred versions
//			mc.getTextureManager().bindTexture(AvatarUiTextures.blurredIcons);
//			drawTexturedModalRect(0, 0, getTextureU(), getTextureV(), 32, 32);
			
		GlStateManager.popMatrix();
		
	}
	//@formatter:on

	public Ability getAbility() {
		return ability;
	}
}
