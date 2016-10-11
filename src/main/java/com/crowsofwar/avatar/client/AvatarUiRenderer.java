package com.crowsofwar.avatar.client;

import java.util.Set;

import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.RadialMenu;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.statctrl.StatusControl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
	
	private static final ResourceLocation STATUS_CONTROL_ICONS = new ResourceLocation("avatarmod",
			"textures/gui/statusControl.png");
	
	private RadialMenu currentBendingMenu;
	private final Minecraft mc;
	
	public AvatarUiRenderer() {
		mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void onGuiRender(RenderGameOverlayEvent.Post e) {
		
		ScaledResolution resolution = e.getResolution();
		
		int mouseX = Mouse.getX() * resolution.getScaledWidth() / mc.displayWidth;
		int mouseY = resolution.getScaledHeight()
				- (Mouse.getY() * resolution.getScaledHeight() / mc.displayHeight);
		
		if (currentBendingMenu != null) {
			if (currentBendingMenu.updateScreen(mouseX, mouseY, resolution)) {
				currentBendingMenu = null;
				mc.setIngameFocus();
			} else {
				currentBendingMenu.drawScreen(mouseX, mouseY, resolution);
			}
		}
		
		Set<StatusControl> statusControls = AvatarMod.proxy.getAllStatusControls();
		for (StatusControl statusControl : statusControls) {
			mc.getTextureManager().bindTexture(STATUS_CONTROL_ICONS);
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
	
	public void openBendingGui(BendingType bending) {
		
		BendingController controller = BendingManager.getBending(bending);
		BendingMenuInfo menu = controller.getRadialMenu();
		
		this.currentBendingMenu = new RadialMenu(menu.getTheme(), menu.getKey(), menu.getButtons());
		mc.setIngameNotInFocus();
		
	}
	
}
