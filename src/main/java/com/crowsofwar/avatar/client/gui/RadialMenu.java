package com.crowsofwar.avatar.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.IAvatarGui;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.util.Raytrace;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class RadialMenu extends GuiScreen implements IAvatarGui {
	
	private static final ResourceLocation radialMenu = new ResourceLocation("avatarmod",
			"textures/gui/radial_segment_cut.png");
	private static final ResourceLocation icons = new ResourceLocation("avatarmod",
			"textures/gui/ability_icons.png");
	private static final ResourceLocation edge = new ResourceLocation("avatarmod",
			"textures/gui/radial_segment_edge_thicker.png");
	
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
	private AvatarControl pressing;
	private BendingAbility[] controls;
	private MenuTheme theme;
	
	public RadialMenu(int controllerId) {
		BendingController controller = BendingManager.getBending(controllerId);
		if (controller == null)
			throw new IllegalArgumentException("Can't make radial menu gui for controller id " + controllerId
					+ " because there is no controller for that Id");
		
		BendingMenuInfo menu = controller.getRadialMenu();
		construct(menu.getTheme(), menu.getKey(), menu.getButtons());
		
	}
	
	/**
	 * Create a new radial menu with the given controls.
	 * 
	 * @param pressing
	 *            The key which must be pressed to keep the GUI open.
	 * @param controls
	 *            A 8-element array of controls. If the arguments passed are less than 8, then the
	 *            array is filled with null. The arguments can only be a maximum of 8.
	 */
	public RadialMenu(MenuTheme theme, AvatarControl pressing, BendingAbility... controls) {
		construct(theme, pressing, controls);
	}
	
	private void construct(MenuTheme theme, AvatarControl pressing, BendingAbility[] controls) {
		this.theme = theme;
		this.segments = new RadialSegment[8];
		this.pressing = pressing;
		
		if (controls == null) {
			throw new IllegalArgumentException("Controls is null");
		}
		if (controls.length > 8) {
			throw new IllegalArgumentException("The length of controls can't be more than 8");
		}
		
		BendingAbility[] ctrl = new BendingAbility[8];
		for (int i = 0; i < ctrl.length; i++) {
			ctrl[i] = i < controls.length ? controls[i] : null;
			System.out.println("Set ability " + i + " to " + ctrl[i]);
		}
		this.controls = ctrl;
		
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		for (int i = 0; i < segments.length; i++) {
			segments[i] = new RadialSegment(this, i, controls[i] == null ? -1 : controls[i].getIconIndex());
		}
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);
		
		for (int i = 0; i < segments.length; i++) {
			if (segments[i] == null) continue;
			System.out.println("Continue to draw segment " + i);
			boolean hover = segments[i].isMouseHover(mouseX, mouseY);
			drawRadialSegment(segments[i], hover);
		}
		
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
	}
	
	@Override
	public void updateScreen() {
		boolean pressed = Keyboard.isKeyDown(AvatarMod.proxy.getKeyHandler().getKeyCode(pressing));
		if (!pressed) {
			int mouseX = getMouseX();
			int mouseY = getMouseY();
			
			for (int i = 0; i < segments.length; i++) {
				if (controls[i] == null) continue;
				if (segments[i].isMouseHover(mouseX, mouseY)) {
					
					Raytrace.Result raytrace = Raytrace.getTargetBlock(mc.thePlayer,
							controls[i].getRaytrace());
					AvatarMod.network.sendToServer(
							new PacketSUseAbility(controls[i], raytrace.getPos(), raytrace.getSide()));
					break;
					
				}
			}
			mc.thePlayer.closeScreen();
		}
	}
	
	/**
	 * Draw the radial segment at that angle and with the specified color.
	 * 
	 * @param segment
	 *            Radial segment to draw
	 * @param background
	 */
	private void drawRadialSegment(RadialSegment segment, boolean hover) {
		
		// Draw background & edge
		GlStateManager.pushMatrix();
		GlStateManager.translate(width / 2f, height / 2f, 0); // Re-center origin
		GlStateManager.scale(menuScale, menuScale, menuScale); // Scale all following arguments
		GlStateManager.rotate(segment.getAngle(), 0, 0, 1); // All transform operations and the
															// image are
		// rotated
		GlStateManager.translate(-segmentX, -segmentY, 0); // Offset the image to the correct
															// center point
		// Draw background
		GlStateManager.color(theme.getBackground().getRed(hover) / 255f,
				theme.getBackground().getGreen(hover) / 255f, theme.getBackground().getBlue(hover) / 255f);
		mc.getTextureManager().bindTexture(radialMenu);
		drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		// Draw edge
		GlStateManager.color(theme.getEdge().getRed(hover) / 255f, theme.getEdge().getGreen(hover) / 255f,
				theme.getEdge().getBlue(hover) / 255f);
		mc.getTextureManager().bindTexture(edge);
		GlStateManager.translate(0, 0, 1);
		drawTexturedModalRect(0, 0, 0, 0, 256, 256);
		GlStateManager.popMatrix();
		
		// Draw icon
		GlStateManager.pushMatrix();
		float iconScale = .8f;
		float angle = segment.getAngle() + 45f;
		angle %= 360;
		GlStateManager.translate(width / 2f, height / 2f, 0); // Re-center origin
		GlStateManager.rotate(angle, 0, 0, 1); // Rotation for next translation
		GlStateManager.translate(-59, -27, 0); // Translate into correct position
		GlStateManager.rotate(-angle, 0, 0, 1); // Icon is now at desired position, rotate the
												// image back
		// to regular
		
		// Color to icon RGB
		GlStateManager.color(theme.getIcon().getRed(hover) / 255f, theme.getIcon().getGreen(hover) / 255f,
				theme.getIcon().getBlue(hover) / 255f);
		
		GlStateManager.translate(0, 0, 2); // Ensure icon is not overlapped
		GlStateManager.scale(iconScale, iconScale, iconScale); // Scale the icon's recentering
																// and actual
		// image
		GlStateManager.translate(-16 * iconScale, -16 * iconScale, 0); // Re-center the icon.
		mc.getTextureManager().bindTexture(icons);
		drawTexturedModalRect(0, 0, segment.getTextureU(), segment.getTextureV(), 32, 32);
		
		GlStateManager.popMatrix();
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
