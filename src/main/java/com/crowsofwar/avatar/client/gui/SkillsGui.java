package com.crowsofwar.avatar.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingAbility;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SkillsGui extends GuiScreen {
	
	private final List<AbilityCard> cards;
	private ScaledResolution res;
	
	public SkillsGui() {
		this.cards = new ArrayList<>();
		cards.add(new AbilityCard(BendingAbility.ABILITY_AIR_JUMP));
	}
	
	@Override
	public void initGui() {
		this.res = new ScaledResolution(mc);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		for (int i = 0; i < cards.size(); i++) {
			cards.get(i).render(res, 30 + i * 40);
		}
		
	}
	
}
