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
	public void render(ScaledResolution res, int xPos) {
		
		AbilityIcon icon = ability.getIcon();
		
		int width = 250;
		int height = (int) (res.getScaledHeight() * 0.6);
		
		GlStateManager.pushMatrix();
			GlStateManager.translate(xPos, (res.getScaledHeight() - height) / 2, 0);
			mc.getTextureManager().bindTexture(AvatarUiTextures.icons);
			drawTexturedModalRect(0, 0, icon.getMinU(), icon.getMinV(), 32, 32);
		GlStateManager.popMatrix();
		
	}
	// @formatter:on
	
}
