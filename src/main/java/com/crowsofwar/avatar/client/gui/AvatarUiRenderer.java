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

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static net.minecraft.client.renderer.GlStateManager.*;

import java.util.Set;

import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * 
 * @author CrowsOfWar
 */
@SideOnly(Side.CLIENT)
public class AvatarUiRenderer extends Gui {
	
	public static AvatarUiRenderer instance;
	
	private RadialMenu currentBendingMenu;
	private RadialSegment fadingSegment;
	private long timeFadeStart;
	private final Minecraft mc;
	private long chiMsgFade;
	
	public AvatarUiRenderer() {
		mc = Minecraft.getMinecraft();
		instance = this;
		chiMsgFade = -1;
	}
	
	@SubscribeEvent
	public void onGuiRender(RenderGameOverlayEvent.Post e) {
		
		if (e.getType() != ElementType.EXPERIENCE) return;
		
		ScaledResolution resolution = e.getResolution();
		
		renderRadialMenu(resolution);
		renderStatusControls(resolution);
		renderChiBar(resolution);
		renderChiMsg(resolution);
		
	}
	
	private void renderRadialMenu(ScaledResolution resolution) {
		int mouseX = Mouse.getX() * resolution.getScaledWidth() / mc.displayWidth;
		int mouseY = resolution.getScaledHeight()
				- (Mouse.getY() * resolution.getScaledHeight() / mc.displayHeight);
		
		if (currentBendingMenu != null) {
			if (currentBendingMenu.updateScreen(mouseX, mouseY, resolution)) {
				currentBendingMenu = null;
				if (!(mc.currentScreen instanceof SkillsGui)) mc.setIngameFocus();
			} else {
				currentBendingMenu.drawScreen(mouseX, mouseY, resolution);
				mc.setIngameNotInFocus();
			}
		}
		if (fadingSegment != null) {
			float timeToFade = 500;
			long timeSinceStart = System.currentTimeMillis() - timeFadeStart;
			if (timeSinceStart > timeToFade) {
				fadingSegment = null;
			} else {
				float scale = (float) (1 + Math.sqrt(timeSinceStart / 10000f));
				fadingSegment.draw(true, resolution,
						(1 - timeSinceStart / timeToFade) * CLIENT_CONFIG.radialMenuAlpha, scale);
			}
		}
	}
	
	private void renderStatusControls(ScaledResolution resolution) {
		Set<StatusControl> statusControls = AvatarPlayerData.fetcher().fetch(mc.thePlayer)
				.getActiveStatusControls();
		for (StatusControl statusControl : statusControls) {
			mc.getTextureManager().bindTexture(AvatarUiTextures.STATUS_CONTROL_ICONS);
			int centerX = resolution.getScaledWidth() / 2;
			int centerY = resolution.getScaledHeight() / 2;
			int xOffset = statusControl.getPosition().xOffset();
			int yOffset = statusControl.getPosition().yOffset();
			
			double scale = .5;
			
			GlStateManager.color(1, 1, 1);
			
			GlStateManager.enableBlend();
			GlStateManager.pushMatrix();
			GlStateManager.scale(scale, scale, scale);
			drawTexturedModalRect((int) ((centerX - xOffset) / scale), (int) ((centerY - yOffset) / scale),
					statusControl.getTextureU(), statusControl.getTextureV(), 16, 16);
			GlStateManager.popMatrix();
		}
	}
	
	private void renderChiBar(ScaledResolution resolution) {
		
		GlStateManager.color(1, 1, 1, 1);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer);
		Chi chi = data.chi();
		float total = chi.getTotalChi();
		float max = chi.getMaxChi();
		float available = chi.getAvailableChi();
		float unavailable = total - available;
		
		// Dimensions of end result in pixels
		float scale = 1.1f;
		float width = 100 * scale;
		float height = 9 * scale;
		
		mc.getTextureManager().bindTexture(AvatarUiTextures.skillsGui);
		
		pushMatrix();
		
		translate(3, resolution.getScaledHeight() - height - 3, 0);
		scale(scale, scale, 1);
		
		// Background of chi bar
		drawTexturedModalRect(0, 0, 0, 36, 100, 9);
		// Available chi
		drawTexturedModalRect((int) (100 * unavailable / max), 0, 1, 27, (int) (100 * available / max), 9);
		// Unavailable chi
		drawTexturedModalRect(0, 0, 0, 45, (int) (100 * unavailable / max), 9);
		
		drawString(mc.fontRendererObj, ((int) total) + "/" + ((int) max) + "," + ((int) available), 0, -20,
				0xffffff);
		
		popMatrix();
		
	}
	
	private void renderChiMsg(ScaledResolution res) {
		
		if (chiMsgFade != -1) {
			
			float seconds = (System.currentTimeMillis() - chiMsgFade) / 1000f;
			float alpha = seconds < 1 ? 1 : 1 - (seconds - 1);
			int alphaI = (int) (alpha * 255);
			// For some reason, any alpha below 4 is displayed at alpha 255
			if (alphaI < 4) alphaI = 4;
			
			String text = TextFormatting.BOLD + I18n.format("avatar.nochi");
			
			//@formatter:off
			drawString(mc.fontRendererObj, text,
					(res.getScaledWidth() - mc.fontRendererObj.getStringWidth(text)) / 2,
					res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 40,
					0xffffff | (alphaI << 24));
			//@formatter:on
			
			if (seconds >= 2) chiMsgFade = -1;
			
		}
		
	}
	
	public static void openBendingGui(BendingType bending) {
		
		BendingController controller = BendingManager.getBending(bending);
		BendingMenuInfo menu = controller.getRadialMenu();
		
		instance.currentBendingMenu = new RadialMenu(controller, menu.getTheme(), menu.getKey(),
				menu.getButtons());
		instance.mc.setIngameNotInFocus();
		
	}
	
	public static boolean hasBendingGui() {
		return instance.currentBendingMenu != null;
	}
	
	public static void fade(RadialSegment segment) {
		instance.fadingSegment = segment;
		instance.timeFadeStart = System.currentTimeMillis();
	}
	
	public static void displayChiMessage() {
		instance.chiMsgFade = System.currentTimeMillis();
	}
	
}
