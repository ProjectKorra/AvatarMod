package com.crowsofwar.avatar.client.gui;

import static com.crowsofwar.avatar.client.gui.RadialMenu.*;

import com.crowsofwar.avatar.common.gui.MenuTheme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Holds information for the RadialMenu about a segment. Contains information on
 * its rotation (position), and whether it's clicked.
 *
 */
public class RadialSegment extends Gui {
	
	private final RadialMenu gui;
	private final MenuTheme theme;
	private final Minecraft mc;
	private final float angle;
	private final int index;
	private final int icon;
	
	public RadialSegment(RadialMenu gui, MenuTheme theme, int index, int icon) {
		this.gui = gui;
		this.angle = 22.5f + index * 45;
		this.index = index;
		if (icon == -1) icon = 255;
		this.icon = icon;
		this.theme = theme;
		this.mc = Minecraft.getMinecraft();
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
		double minAngle = angle - 44;
		if (minAngle < 0) minAngle += 360;
		double maxAngle = angle;
		boolean addCurrentAngle = currentAngle < 0;
		if (minAngle > maxAngle) {
			maxAngle += 360;
			addCurrentAngle = true;
		}
		if (addCurrentAngle) currentAngle += 360;
		
		return r >= 100 && r <= 200 && currentAngle >= minAngle && currentAngle <= maxAngle;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public int getTextureU() {
		return (icon * 32) % 256;
	}
	
	public int getTextureV() {
		return (icon / 8) * 32;
	}
	
	/**
	 * Draw this radial segment.
	 * 
	 * @param hover
	 *            Whether mouse is over it
	 * @param resolution
	 *            Resolution MC is at
	 */
	public void draw(boolean hover, ScaledResolution resolution) {
		draw(hover, resolution, 1, 1);
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
			GlStateManager.rotate(this.getAngle(), 0, 0, 1);		// All transform operations and the image are rotated
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(-segmentX, -segmentY, 0);		// Offset the image to the correct
																	// center point
			// Draw background
			GlStateManager.color(theme.getBackground().getRed(hover) / 255f,
					theme.getBackground().getGreen(hover) / 255f, theme.getBackground().getBlue(hover) / 255f, alpha);
			mc.getTextureManager().bindTexture(radialMenu);
			drawTexturedModalRect(0, 0, 0, 0, 256, 256);
			// Draw edge
			GlStateManager.color(theme.getEdge().getRed(hover) / 255f, theme.getEdge().getGreen(hover) / 255f,
					theme.getEdge().getBlue(hover) / 255f, alpha);
			mc.getTextureManager().bindTexture(edge);
//			GlStateManager.translate(0, 0, 1);
			drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		GlStateManager.popMatrix();
		
		// Draw icon
		GlStateManager.pushMatrix();
			float iconScale = .8f;
			float angle = this.getAngle() + 45f;
			angle %= 360;
			
			GlStateManager.translate(width / 2f, height / 2f, 0); // Re-center origin
			GlStateManager.rotate(angle, 0, 0, 1); // Rotation for next translation
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(-59, -27, 0); // Translate into correct position
			GlStateManager.rotate(-angle, 0, 0, 1); // Icon is now at desired position, rotate the
													// image back
			// to regular
			
			// Color to icon RGB
			GlStateManager.color(theme.getIcon().getRed(hover) / 255f, theme.getIcon().getGreen(hover) / 255f,
					theme.getIcon().getBlue(hover) / 255f, alpha);
			
			GlStateManager.translate(0, 0, 2); // Ensure icon is not overlapped
			GlStateManager.scale(iconScale, iconScale, iconScale); // Scale the icon's recentering
																	// and actual
			// image
			GlStateManager.translate(-16 * iconScale, -16 * iconScale, 0); // Re-center the icon.
			mc.getTextureManager().bindTexture(RadialMenu.icons);
			drawTexturedModalRect(0, 0, getTextureU(), getTextureV(), 32, 32);
			
		GlStateManager.popMatrix();
		
	}
	//@formatter:on
	
}
