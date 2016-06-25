package com.maxandnoah.avatar.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.maxandnoah.avatar.AvatarMod;
import com.maxandnoah.avatar.client.controls.AvatarKeybinding;
import com.maxandnoah.avatar.common.AvatarAbility;
import com.maxandnoah.avatar.common.IControlsHandler;
import com.maxandnoah.avatar.common.gui.IAvatarGui;
import com.maxandnoah.avatar.common.network.packets.PacketSUseAbility;
import com.maxandnoah.avatar.common.util.Raytrace;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class RadialMenu extends GuiScreen implements IAvatarGui {
	
	private static final ResourceLocation radialMenu = new ResourceLocation("avatarmod", "textures/gui/radial_segment.png");
	private static final ResourceLocation icons = new ResourceLocation("avatarmod", "textures/gui/ability_icons.png");
	
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
	private AvatarKeybinding pressing;
	private AvatarAbility[] controls;
	
	/**
	 * Create a new radial menu with the given controls.
	 * @param pressing The key which must be pressed to keep the GUI open.
	 * @param controls A 8-element array of controls. If the arguments passed
	 * are less than 8, then the array is filled with {@link AvatarAbility#NONE}.
	 * The arguments can only be a maximum of 8.
	 */
	public RadialMenu(AvatarKeybinding pressing, AvatarAbility... controls) {
		this.segments = new RadialSegment[8];
		this.pressing = pressing;
		
		if (controls == null) throw new IllegalArgumentException("Controls is null");
		if (controls.length > 8) throw new IllegalArgumentException("The length of controls can't be more than 8");
		AvatarAbility[] ctrl = new AvatarAbility[8];
		for (int i = 0; i < ctrl.length; i++) {
			if (i < controls.length) ctrl[i] = controls[i]; else ctrl[i] = AvatarAbility.NONE;
		}
		this.controls = ctrl;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		for (int i = 0; i < segments.length; i++) {
			segments[i] = new RadialSegment(this, i);
		}
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);
		
		for (int i = 0; i < segments.length; i++) {
			int br = 225, bg = 225, bb = 225;
			int ir = 110, ig = 110, ib = 110;
			
			if (segments[i].isMouseHover(mouseX, mouseY)) {
				br = 133;
				bg = 194;
				bb = 214;
				ir = ig = ib = 255;
			}
			
			drawRadialSegment(segments[i], br, bg, bb, ir, ig, ib);
			
		}
		
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
	}
	
	@Override
	public void updateScreen() {
		boolean pressed = Keyboard.isKeyDown(getKeyHandler().getKeyCode(AvatarKeybinding.KEY_RADIAL_MENU));
		if (!pressed) {
			int mouseX = getMouseX();
			int mouseY = getMouseY();
			
			for (int i = 0; i < segments.length; i++) {
				if (segments[i].isMouseHover(mouseX, mouseY)) {
					AvatarMod.network.sendToServer(new PacketSUseAbility(controls[i],
							Raytrace.getTargetBlock(mc.thePlayer, -1)));
					break;
				}
			}
			mc.thePlayer.closeScreen();
		}
	}
	
	/**
	 * Draw the radial segment at that angle and with the specified color.
	 * @param segment Radial segment to draw
	 * @param r Red component of the color, 0-255
	 * @param g Green component of the color, 0-255
	 * @param b Blue component of the color, 0-255
	 */
	private void drawRadialSegment(RadialSegment segment, int r, int g, int b,
			int iconR, int iconG, int iconB) {
		ResourceLocation rm = new ResourceLocation("avatarmod", "textures/gui/radial_segment_cut.png");
		
		mc.getTextureManager().bindTexture(rm);
		GL11.glPushMatrix();
		GL11.glTranslatef(width / 2f, height / 2f, 0);	// Re-center origin
		GL11.glScalef(menuScale, menuScale, menuScale);	// Scale all following arguments
		GL11.glRotatef(segment.getAngle(), 0, 0, 1);	// All transform operations and the image are rotated
		GL11.glTranslatef(-segmentX, -segmentY, 0);		// Offset the image to the correct center point
		GL11.glColor3f(r / 255f, g / 255f, b / 255f);
		drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		float iconScale = 1.5f;
		float angle = segment.getAngle() + 45f;
		angle %= 360;
		GL11.glTranslatef(width / 2f, height / 2f, 0);	// Re-center origin
		GL11.glScalef(iconScale, iconScale, iconScale);
		GL11.glRotatef(angle, 0, 0, 1);		// Rotation for next translation
		GL11.glTranslatef(-59 / iconScale, -27 / iconScale, 0);		// Translate into correct position
		GL11.glRotatef(-angle, 0, 0, 1);	// Icon is now at desired position, rotate the image back to regular
		GL11.glTranslatef(-8, -8, 0);		// Re-center the icon.
		GL11.glColor3f(iconR / 255f, iconG / 255f, iconB / 255f);			// Set color icon
		GL11.glTranslatef(0, 0, 1); 		// Ensure icon is not overlapped by the radial segment picture
		mc.getTextureManager().bindTexture(icons);
		drawTexturedModalRect(0, 0, segment.getTextureU(), segment.getTextureV(), 16, 16);
		
		GL11.glPopMatrix();
	}
	
	private int getMouseX() {
		return Mouse.getEventX() * this.width / this.mc.displayWidth;
	}
	
	private int getMouseY() {
		return this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
	}
	
	private IControlsHandler getKeyHandler() {
		return AvatarMod.proxy.getKeyHandler();
	}
	
}
