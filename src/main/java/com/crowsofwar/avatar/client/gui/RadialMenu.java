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

import static com.crowsofwar.avatar.AvatarMod.proxy;
import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.gorecore.format.FormattedMessage.newChatMessage;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.format.FormattedMessage;
import com.crowsofwar.gorecore.format.FormattedMessageProcessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;

public class RadialMenu extends Gui {
	
	private static final FormattedMessage MSG_RADIAL_XP = newChatMessage("avatar.radial.xp", "level", "xp");
	
	/**
	 * Center of rotation X position for radial_segment.png
	 */
	public static final int segmentX = 207;
	/**
	 * Center of rotation Y position for radial_segment.png
	 */
	public static final int segmentY = 296;
	/**
	 * Scaling factor for the radial menu
	 */
	public static final float menuScale = 0.36f;
	
	private RadialSegment[] segments;
	private BendingAbility[] controls;
	private MenuTheme theme;
	private final BendingController controller;
	
	/**
	 * Current radial segment that the mouse is over, null for none.
	 */
	private RadialSegment prevMouseover;
	
	private int ticksExisted;
	
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
	public RadialMenu(BendingController controller, MenuTheme theme, BendingAbility... controls) {
		this.controller = controller;
		this.theme = theme;
		this.segments = new RadialSegment[8];
		this.ticksExisted = 0;
		
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
			segments[i] = new RadialSegment(this, theme, i, controls[i]);
		}
		
	}
	
	public void drawScreen(int mouseX, int mouseY, ScaledResolution resolution) {
		
		float scale = ticksExisted <= 10 ? 0.5f + (float) Math.sqrt(ticksExisted / 40f) : 1;
		
		for (int i = 0; i < segments.length; i++) {
			if (segments[i] == null) continue;
			boolean hover = segments[i].isMouseHover(mouseX, mouseY, resolution);
			segments[i].draw(hover, resolution, scale * CLIENT_CONFIG.radialMenuAlpha, scale);
			
			if (hover) {
				displaySegmentDetails(controls[i], resolution);
			}
		}
		
	}
	
	private void displaySegmentDetails(BendingAbility ability, ScaledResolution resolution) {
		
		String nameKey = ability == null ? "avatar.ability.undefined" : "avatar.ability." + ability.getName();
		int x = resolution.getScaledWidth() / 2;
		int y = (int) (resolution.getScaledHeight() / 2 - mc.fontRenderer.FONT_HEIGHT * 1.5);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.player);
		if (data != null) {
			
			AbilityData abilityData = data.getAbilityData(ability);
			
			String secondKey = "avatar.radial.undefined";
			String[] secondArgs = { "", "" };
			if (ability != null) {
				secondKey = "avatar.radial.xp";
				secondArgs[0] = abilityData.getLevel() + "";
				secondArgs[1] = (int) (abilityData.getXp()) + "";
				
				if (abilityData.getLevel() == 3) {
					String path = abilityData.getPath() == AbilityTreePath.FIRST ? "1" : "2";
					secondKey = nameKey + ".lvl4_" + path;
				}
				boolean creative = mc.player.capabilities.isCreativeMode;
				if (abilityData.isLocked() && !creative) {
					secondKey = "avatar.radial.locked2";
					secondArgs[0] = AvatarMod.proxy.getKeyHandler().getDisplayName(AvatarControl.KEY_SKILLS)
							+ "";
					nameKey = "avatar.radial.locked1";
				}
				if (abilityData.isLocked() && creative) {
					secondKey = "avatar.radial.lockedCreative2";
					nameKey = "avatar.radial.lockedCreative1";
				}
				
			}
			String second = I18n.format(secondKey);
			
			// in the case of level 4 upgrades, the upgrade name is displayed
			// cut out the second line
			if (second.contains(" ;; ")) {
				second = second.substring(0, second.indexOf(" ;; "));
			}
			
			second = FormattedMessageProcessor.formatText(MSG_RADIAL_XP, second,
					(Object[]) ArrayUtils.addAll(secondArgs, abilityData.getLevel() + ""));
			
			drawCenteredString(mc.fontRenderer, second, x,
					(int) (resolution.getScaledHeight() / 2 + mc.fontRenderer.FONT_HEIGHT * 0.5),
					0xffffff);
			
		}
		
		drawCenteredString(mc.fontRenderer, I18n.format(nameKey), x, y, 0xffffff);
		
	}
	
	private void playClickSound(float pitch) {
		mc.getSoundHandler()
				.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, pitch));
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
		
		ticksExisted++;
		
		boolean closeGui = !Keyboard
				.isKeyDown(proxy.getKeyHandler().getKeyCode(AvatarControl.KEY_USE_BENDING))
				|| AvatarControl.CONTROL_LEFT_CLICK.isPressed();
		
		// Find current mouse over
		RadialSegment currentMouseover = null;
		for (RadialSegment segment : segments) {
			if (segment.isMouseHover(mouseX, mouseY, resolution)) {
				currentMouseover = segment;
				break;
			}
		}
		
		if (currentMouseover != null && currentMouseover != prevMouseover) {
			playClickSound(1.3f);
		}
		prevMouseover = currentMouseover;
		if (currentMouseover == null) {
			
			int centerX = resolution.getScaledWidth() / 2, centerY = resolution.getScaledHeight() / 2;
			MenuTheme theme = controller.getRadialMenu().getTheme();
			
			drawCenteredString(mc.fontRenderer,
					"" + TextFormatting.BOLD + I18n.format("avatar." + controller.getControllerName()),
					centerX, centerY - mc.fontRenderer.FONT_HEIGHT, theme.getText());
			
		}
		
		if (closeGui) {
			
			for (int i = 0; i < segments.length; i++) {
				if (controls[i] == null) continue;
				if (segments[i].isMouseHover(mouseX, mouseY, resolution)) {
					
					Raytrace.Result raytrace = Raytrace.getTargetBlock(mc.player,
							controls[i].getRaytrace());
					AvatarMod.network.sendToServer(new PacketSUseAbility(controls[i], raytrace));
					AvatarUiRenderer.fade(segments[i]);
					playClickSound(0.8f);
					break;
					
				}
			}
			
		}
		return closeGui;
	}
	
}
