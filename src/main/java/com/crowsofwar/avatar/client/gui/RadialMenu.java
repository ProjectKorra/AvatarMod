package com.crowsofwar.avatar.client.gui;

import static com.crowsofwar.avatar.AvatarMod.proxy;

import org.lwjgl.input.Keyboard;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.AvatarUiRenderer;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.util.Raytrace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class RadialMenu extends Gui {
	
	static final ResourceLocation radialMenu = new ResourceLocation("avatarmod",
			"textures/gui/radial_segment_cut.png");
	static final ResourceLocation icons = new ResourceLocation("avatarmod", "textures/gui/ability_icons.png");
	static final ResourceLocation edge = new ResourceLocation("avatarmod",
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
	
	private final Minecraft mc = Minecraft.getMinecraft();
	
	/**
	 * Create a new radial menu with the given controls.
	 * 
	 * @param pressing
	 *            The key which must be pressed to keep the GUI open.
	 * @param controls
	 *            A 8-element array of controls. If the arguments passed are
	 *            less than 8, then the array is filled with null. The arguments
	 *            can only be a maximum of 8.
	 */
	public RadialMenu(MenuTheme theme, AvatarControl pressing, BendingAbility... controls) {
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
		}
		this.controls = ctrl;
		
		for (int i = 0; i < segments.length; i++) {
			segments[i] = new RadialSegment(this, theme, i,
					controls[i] == null ? -1 : controls[i].getIconIndex());
		}
		
		ScaledResolution resolution = new ScaledResolution(mc);
		
	}
	
	public void drawScreen(int mouseX, int mouseY, ScaledResolution resolution) {
		
		for (int i = 0; i < segments.length; i++) {
			if (segments[i] == null) continue;
			boolean hover = segments[i].isMouseHover(mouseX, mouseY, resolution);
			segments[i].draw(hover, resolution);
		}
		
	}
	
	/**
	 * Handle key release. Triggers new abilities if possible.
	 * 
	 * @param mouseX
	 *            Mouse x-pos
	 * @param mouseY
	 *            Mouse y-pos
	 * @return Whether to close the screen
	 */
	public boolean updateScreen(int mouseX, int mouseY, ScaledResolution resolution) {
		
		boolean closeGui = !Keyboard.isKeyDown(proxy.getKeyHandler().getKeyCode(pressing))
				|| AvatarMod.proxy.getKeyHandler().isControlPressed(AvatarControl.CONTROL_LEFT_CLICK);
		
		if (closeGui) {
			
			for (int i = 0; i < segments.length; i++) {
				if (controls[i] == null) continue;
				if (segments[i].isMouseHover(mouseX, mouseY, resolution)) {
					
					Raytrace.Result raytrace = Raytrace.getTargetBlock(mc.thePlayer,
							controls[i].getRaytrace());
					AvatarMod.network.sendToServer(
							new PacketSUseAbility(controls[i], raytrace.getPos(), raytrace.getSide()));
					AvatarUiRenderer.fade(segments[i]);
					break;
					
				}
			}
			
		}
		return closeGui;
	}
	
}
