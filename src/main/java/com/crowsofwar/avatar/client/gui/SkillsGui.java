package com.crowsofwar.avatar.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingAbility;

import net.minecraft.client.gui.GuiScreen;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SkillsGui extends GuiScreen {
	
	private final List<AbilityCard> cards;
	
	public SkillsGui() {
		this.cards = new ArrayList<>();
		cards.add(new AbilityCard(BendingAbility.ABILITY_AIR_JUMP));
	}
	
}
