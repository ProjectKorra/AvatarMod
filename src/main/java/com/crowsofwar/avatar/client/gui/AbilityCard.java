package com.crowsofwar.avatar.client.gui;

import static net.minecraft.client.renderer.GlStateManager.*;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.AbilityIcon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityCard extends Gui {
	
	private final BendingAbility ability;
	private final AvatarPlayerData data;
	private final Minecraft mc;
	
	public AbilityCard(BendingAbility ability) {
		this.mc = Minecraft.getMinecraft();
		this.ability = ability;
		this.data = AvatarPlayerData.fetcher().fetchPerformance(mc.thePlayer);
	}
	
	public BendingAbility getAbility() {
		return ability;
	}
	
	// @formatter:off
	public void render(ScaledResolution res, int index) {
		
		// NOTE! Minecraft has automatic icon scaling; can be found via res.getScaleFactor()
		// To counteract this, normally you would use
		//   GlStateManager.scale(1f / res.getScaleFactor, 1f / res.getScaleFactor(), 1)
		// HOWEVER, since this is calculating scale already, I don't need to use that
		
		// There are 2 types of pixels here.
		// SCREEN PIXELS - The actual pixels of the screen. Requires resolution to make sure everything is proportioned.
		// CARD PIXELS   - Using scaling seen below, the card is now 100px x ??px (height depends on resolution).
		
		GlStateManager.enableBlend();
		
		AbilityIcon icon = ability.getIcon();
		
		float spacing = res.getScaledWidth() / 8.5f; // Spacing between each card
		float actualWidth = res.getScaledWidth() / 7f;  // Width of each card;  1/10 of total width
		float height = res.getScaledHeight() * 0.6f; // Height of each card; about 1/2 of total height
		float scaledWidth = 100;
		
		float scale = actualWidth / scaledWidth;
		
		float minX = (int) (index * (actualWidth + spacing));
		float minY = (res.getScaledHeight() - height) / 2;
		float maxX = minX + actualWidth;
		float maxY = minY + height;
		float midX = (minX + maxX) / 2;
		float midY = (minY + maxY) / 2;
		
		float padding = 10;
		float leftX = minX + padding;
		float rightX = maxX - padding;
		float innerWidth = scaledWidth - 2 * padding;
		
		float iconY = 25;
		float iconWidth = 80;
		float iconHeight = 80;
		
		float textMinX = 5;
		float textMaxX = 95;
		float textY = 123;
		
		float progressY = 120;
		
		// Draw card background
		pushMatrix();
			translate(minX, minY, 0);
			scale(actualWidth, height, 1);
			renderImage(AvatarUiTextures.skillsGui, 0, 0, 1, 1);
		popMatrix();
		
		pushMatrix();
			translate(minX, minY, 0);
			scale(scale, scale, 1);
			// Now is translated & scaled to size of 100px width (height is variable)
			
			// draw icon
			pushMatrix();
				translate(padding, iconY, 0);
				scale(iconWidth / 32, iconHeight / 32, 1);
				renderImage(AvatarUiTextures.icons, icon.getMinU(), icon.getMinV(), 32, 32);
			popMatrix();
			
			// draw progress bar
			pushMatrix();
				translate(10, progressY, 0);
				scale(iconWidth / 40, iconWidth / 40, 1);
				renderImage(AvatarUiTextures.skillsGui, 0, 1, 40, 13);
				renderImage(AvatarUiTextures.skillsGui, 0, 14, (int) (data.getAbilityData(ability).getXp() / scaledWidth * 40), 13);
			popMatrix();
			
		popMatrix();
		
		pushMatrix();
			
			String draw = ((int) data.getAbilityData(ability).getXp()) + "%";
			
			translate(minX, minY, 0);
			scale(scale, scale, 1);
			
			renderCenteredString(draw, textY, 2.5f);
			renderCenteredString("HELLO", 10, 3f);
			
		popMatrix();
		
	}
	// @formatter:on
	
	/**
	 * Draws the image. Any transformations (e.g. transformation) should be
	 * performed with OpenGL functions.
	 * 
	 * @param texture
	 *            The texture to draw
	 * @param u
	 *            Leftmost U coordinate
	 * @param v
	 *            Uppermost V coordinate
	 * @param width
	 *            Width in pixels from texture
	 * @param height
	 *            Height in pixels from texture
	 */
	private void renderImage(ResourceLocation texture, int u, int v, int width, int height) {
		mc.renderEngine.bindTexture(texture);
		drawTexturedModalRect(0, 0, u, v, width, height);
	}
	
	/**
	 * Draws a centered string at the given y-position. Assumes that has already
	 * been transformed to the top-left corner of card (without padding), and
	 * the card is 100px wide. Padding is 10px.
	 * 
	 * @param str
	 *            String to draw
	 * @param y
	 *            Y position to draw at
	 * @param scale
	 *            Scale of text
	 */
	private void renderCenteredString(String str, float y, float scale) {
		pushMatrix();
		// assume padding is 10, innerWidth is 80
		translate(10 + (80 - mc.fontRendererObj.getStringWidth(str) * scale) / 2, y, 0);
		scale(scale, scale, 1);
		drawString(mc.fontRendererObj, str, 0, 0, 0xffffff);
		popMatrix();
	}
	
}
