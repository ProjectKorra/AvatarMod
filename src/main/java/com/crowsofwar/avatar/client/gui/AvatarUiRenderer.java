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

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;
import com.crowsofwar.avatar.common.entity.EntityIcePrison;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static com.crowsofwar.avatar.client.gui.AvatarUiTextures.BLOCK_BREAK;
import static com.crowsofwar.avatar.client.uitools.ScreenInfo.*;
import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.common.entity.EntityIcePrison.IMPRISONED_TIME;
import static net.minecraft.client.renderer.GlStateManager.*;

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
	private long errorMsgFade;
	private String errorMsg;
	
	public AvatarUiRenderer() {
		mc = Minecraft.getMinecraft();
		instance = this;
		errorMsgFade = -1;
		errorMsg = "";
	}
	
	@SubscribeEvent
	public void onGuiRender(RenderGameOverlayEvent.Post e) {
		
		if (e.getType() != ElementType.EXPERIENCE) return;
		
		ScaledResolution resolution = e.getResolution();
		
		renderRadialMenu(resolution);
		renderStatusControls(resolution);
		renderChiBar(resolution);
		renderChiMsg(resolution);
		renderActiveBending(resolution);
		renderAirBubbleHealth(resolution);
		renderPrisonCracks(resolution);
		
	}
	
	private void renderRadialMenu(ScaledResolution resolution) {
		int mouseX = Mouse.getX() * resolution.getScaledWidth() / mc.displayWidth;
		int mouseY = resolution.getScaledHeight()
				- (Mouse.getY() * resolution.getScaledHeight() / mc.displayHeight);
		
		// For some reason, not including this will cause weirdness in 3rd
		// person
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		
		if (currentBendingMenu != null) {
			if (currentBendingMenu.updateScreen(mouseX, mouseY, resolution)) {
				currentBendingMenu = null;
				mc.setIngameFocus();
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
		List<StatusControl> statusControls = AvatarPlayerData.fetcher().fetch(mc.player)
				.getAllStatusControls();
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
		
		GlStateManager.color(1, 1, 1, CLIENT_CONFIG.chiBarAlpha);
		
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.player);
		
		if (data.getAllBending().isEmpty()) return;
		
		Chi chi = data.chi();
		float total = chi.getTotalChi();
		float max = chi.getMaxChi();
		float available = chi.getAvailableChi();
		float unavailable = total - available;
		
		// Dimensions of end result in pixels
		float scale = 1.1f;
		float width = 100 * scale;
		float height = 9 * scale;
		
		mc.getTextureManager().bindTexture(AvatarUiTextures.CHI_BAR);
		
		pushMatrix();
		
		translate(3, resolution.getScaledHeight() - height - 3, 0);
		scale(scale, scale, 1);
		
		// Background of chi bar
		drawTexturedModalRect(0, 0, 0, 36, 100, 9);
		
		// Available chi
		
		float unadjustedU = 100 * unavailable / max;
		int adjustedU = (int) Math.floor(unadjustedU / 8f) * 8;
		float uDiff = unadjustedU - adjustedU;
		
		drawTexturedModalRect(adjustedU, 0, 1, 27, (int) (100 * available / max + uDiff), 9);
		
		// Unavailable chi
		drawTexturedModalRect(0, 0, 0, 45, (int) (100 * unavailable / max), 9);
		
		drawString(mc.fontRenderer, ((int) total) + "/" + ((int) max) + "," + ((int) available), 0, -20,
				0xffffff | ((int) (CLIENT_CONFIG.chiBarAlpha * 255) << 24));
		
		popMatrix();
		
	}
	
	private void renderChiMsg(ScaledResolution res) {
		
		if (errorMsgFade != -1) {
			
			float seconds = (System.currentTimeMillis() - errorMsgFade) / 1000f;
			float alpha = seconds < 1 ? 1 : 1 - (seconds - 1);
			int alphaI = (int) (alpha * 255);
			// For some reason, any alpha below 4 is displayed at alpha 255
			if (alphaI < 4) alphaI = 4;
			
			String text = TextFormatting.BOLD + I18n.format(errorMsg);
			
			//@formatter:off
			drawString(mc.fontRenderer, text,
					(res.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2,
					res.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT - 40,
					0xffffff | (alphaI << 24));
			//@formatter:on
			
			if (seconds >= 2) errorMsgFade = -1;
			
		}
		
	}
	
	private void renderActiveBending(ScaledResolution res) {
		
		BendingData data = AvatarPlayerData.fetcher().fetch(mc.player);
		
		if (data.getActiveBending() != null) {
			
			GlStateManager.color(1, 1, 1, CLIENT_CONFIG.bendingCycleAlpha);
			drawBendingIcon(0, 0, data.getActiveBending());
			
			GlStateManager.color(1, 1, 1, CLIENT_CONFIG.bendingCycleAlpha * 0.5f);
			List<BendingStyle> allBending = data.getAllBending();
			
			// Draw next
			int indexNext = allBending.indexOf(data.getActiveBending()) + 1;
			if (indexNext == allBending.size()) indexNext = 0;
			
			if (allBending.size() > 1) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, -1);
				drawBendingIcon(25, 25, allBending.get(indexNext));
				GlStateManager.popMatrix();
			}
			
			// Draw previous
			int indexPrevious = allBending.indexOf(data.getActiveBending()) - 1;
			if (indexPrevious <= -1) indexPrevious = allBending.size() - 1;
			
			if (allBending.size() > 2) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, -1);
				drawBendingIcon(-25, 25, allBending.get(indexPrevious));
				GlStateManager.popMatrix();
			}
			
		}
		
	}
	
	private void drawBendingIcon(int xOff, int yOff, BendingStyle controller) {
		int x = screenWidth() / scaleFactor() - 85 + xOff;
		int y = screenHeight() / scaleFactor() - 60 + yOff;
		int u = 50 * (controller.getId() - 1);
		int v = 137;
		mc.renderEngine.bindTexture(AvatarUiTextures.skillsGui);
		drawTexturedModalRect(x, y, u, v, 50, 50);
	}
	
	private void renderAirBubbleHealth(ScaledResolution res) {
		
		mc.renderEngine.bindTexture(AvatarUiTextures.airBubbleHealth);
		GlStateManager.color(1, 1, 1, 1);
		
		World world = mc.world;
		EntityPlayer player = mc.player;
		BendingData data = Bender.getData(player);
		
		if (data.hasStatusControl(StatusControl.BUBBLE_CONTRACT)) {
			EntityAirBubble bubble = EntityAirBubble.lookupControlledEntity(world, EntityAirBubble.class,
					player);
			if (bubble != null) {
				
				int x = res.getScaledWidth() / 2 - 91;
				int y = res.getScaledHeight() - GuiIngameForge.left_height;
				if (mc.player.getTotalArmorValue() == 0) {
					y += 10;
				}
				
				int hearts = (int) (bubble.getMaxHealth() / 2);
				for (int i = 0; i < hearts; i++) {
					
					// Draw background
					drawTexturedModalRect(x + i * 9, y, 0, 0, 9, 9);
					
					// Draw hearts or half hearts
					int diff = (int) (bubble.getHealth() - i * 2);
					if (diff >= 2) {
						drawTexturedModalRect(x + i * 9, y, 18, 0, 9, 9);
					} else if (diff == 1) {
						drawTexturedModalRect(x + i * 9, y, 27, 0, 9, 9);
					}
					
				}
				
			}
		}
		
	}
	
	private void renderPrisonCracks(ScaledResolution res) {
		
		EntityPlayer player = mc.player;
		EntityIcePrison prison = EntityIcePrison.getPrison(player);
		if (prison != null) {
			
			GlStateManager.pushMatrix();
			
			float scaledWidth = res.getScaledWidth();
			float scaledHeight = res.getScaledHeight();
			float scaleX = scaledWidth / 256;
			float scaleY = scaledHeight / 256;
			float scale = Math.max(scaleX, scaleY);
			
			// Width of screen: scaledWidth
			// Width of ice: scale * 256
			
			GlStateManager.translate((scaledWidth - scale * 256) / 2, (scaledHeight - scale * 256) / 2, 0);
			GlStateManager.scale(scale, scale, 1);
			
			mc.renderEngine.bindTexture(AvatarUiTextures.ICE);
			drawTexturedModalRect(0, 0, 0, 0, 256, 256);
			
			color(1, 1, 1, 0.5f);
			float percent = (float) prison.ticksExisted / IMPRISONED_TIME;
			int crackIndex = (int) (percent * percent * percent * (BLOCK_BREAK.length + 1)) - 1;
			if (crackIndex > -1) {
				mc.renderEngine.bindTexture(BLOCK_BREAK[crackIndex]);
				drawTexturedModalRect(0, 0, 0, 0, 256, 256);
			}
			
			GlStateManager.popMatrix();
			
		}
		
	}
	
	public static void openBendingGui(int bending) {
		
		BendingStyle controller = BendingManager.getBending(bending);
		BendingMenuInfo menu = controller.getRadialMenu();
		
		instance.currentBendingMenu = new RadialMenu(controller, menu.getTheme(), menu.getButtons());
		instance.mc.setIngameNotInFocus();
		
	}
	
	public static boolean hasBendingGui() {
		return instance.currentBendingMenu != null;
	}
	
	public static void fade(RadialSegment segment) {
		instance.fadingSegment = segment;
		instance.timeFadeStart = System.currentTimeMillis();
	}
	
	public static void displayErrorMessage(String message) {
		instance.errorMsgFade = System.currentTimeMillis();
		instance.errorMsg = message;
	}
	
}
