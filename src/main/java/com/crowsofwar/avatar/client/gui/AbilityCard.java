package com.crowsofwar.avatar.client.gui;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.AbilityIcon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

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
		
		AbilityIcon icon = ability.getIcon();
		
		int width = (int) (res.getScaledWidth() / 15.0);
		int height = (int) (res.getScaledHeight() * 0.6);
		
		float scale = width / 32f;
		
		int xPos = (int) (index * res.getScaledWidth() / 10f);
		int yPos = (res.getScaledHeight() - height) / 2;
		
		GlStateManager.pushMatrix();
			GlStateManager.translate(xPos, yPos, 0);
			GlStateManager.scale(scale, scale, 1);
			mc.getTextureManager().bindTexture(AvatarUiTextures.icons);
			drawTexturedModalRect(0, 0, icon.getMinU(), icon.getMinV(), 32, 32);
			
		GlStateManager.popMatrix();
		drawString(mc.fontRendererObj, ((int) data.getAbilityData(ability).getXp()) + "%", xPos, yPos + 40, 0xffffff);
		
	}
	// @formatter:on
	
}
