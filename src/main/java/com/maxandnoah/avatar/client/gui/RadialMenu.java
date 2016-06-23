package com.maxandnoah.avatar.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.maxandnoah.avatar.common.gui.IAvatarGui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class RadialMenu extends GuiScreen implements IAvatarGui {
	
	private static final ResourceLocation radialMenu = new ResourceLocation("avatarmod", "textures/gui/radial_segment.png");
	
	/**
	 * Center of rotation X position for radial_segment.png
	 */
	public static final int segmentX = 141;
	/**
	 * Center of rotation Y position for radial_segment.png
	 */
	public static final int segmentY = 200;
	/**
	 * Scaling factor for the radial menu
	 */
	public static final float menuScale = 0.4f;
	
	private RadialSegment[] segments;
	
	public RadialMenu() {
		segments = new RadialSegment[8];
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		for (int i = 0; i < segments.length; i++) {
			segments[i] = new RadialSegment(this, 20 + i * 45);
		}
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);
		
		for (int i = 0; i < segments.length; i++) {
			int r = 115, g = 115,b = 115;
			
			if (segments[i].isMouseHover(mouseX, mouseY)) {
				r = 133;
				g = 194;
				b = 214;
			}
			
			drawRadialSegment(segments[i].getAngle(), r, g, b);
			
		}
		
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		System.out.println("clicked mouse button: " + mouseButton);
		
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].isMouseHover(mouseX, mouseY)) {
				System.out.println("Clicked: " + i);
			}
		}
		
	}
	
	/**
	 * Draw the radial segment at that angle and with gray as the color.
	 * @param angle Angle in degrees
	 */
	private void drawRadialSegment(float angle) {
		drawRadialSegment(angle, 80, 80, 80);
	}
	
	/**
	 * Draw the radial segment at that angle and with the specified color.
	 * @param angle Angle in degrees
	 * @param r Red component of the color, 0-255
	 * @param g Green component of the color, 0-255
	 * @param b Blue component of the color, 0-255
	 */
	private void drawRadialSegment(float angle, int r, int g, int b) {
		ResourceLocation rm = new ResourceLocation("avatarmod", "textures/gui/radial_segment_cut.png");

		mc.getTextureManager().bindTexture(rm);
		GL11.glPushMatrix();
		GL11.glTranslatef(width / 2f, height / 2f, 0);	// Re-center origin
		GL11.glScalef(menuScale, menuScale, menuScale);	// Scale all following arguments
		GL11.glRotatef(angle, 0, 0, 1);					// All transform operations and the image are rotated
		GL11.glTranslatef(-segmentX, -segmentY, 0);		// Offset the image to the correct center point
		GL11.glColor3f(r / 255f, g / 255f, b / 255f);
		drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		GL11.glPopMatrix();
	}
	
}
