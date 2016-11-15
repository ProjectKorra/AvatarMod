package com.crowsofwar.avatar.client.gui;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

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
	
	public void render(ScaledResolution res, int xPos) {
		
		int width = 250;
		int height = (int) (res.getScaledHeight() * 0.6);
		
		mc.getTextureManager().bindTexture(AvatarUiTextures.icons);
		drawTexturedModalRect(xPos, (res.getScaledHeight() - height) / 2, 0, 16, 0, 16);
	}
	
}
