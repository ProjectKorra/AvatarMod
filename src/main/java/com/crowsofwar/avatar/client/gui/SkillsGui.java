package com.crowsofwar.avatar.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

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
	
	private int scroll;
	private int startScroll;
	private int progressScroll;
	
	private boolean wasMouseDown;
	
	public SkillsGui() {
		this.cards = new ArrayList<>();
		cards.add(new AbilityCard(BendingAbility.ABILITY_AIR_JUMP));
		cards.add(new AbilityCard(BendingAbility.ABILITY_FIRE_ARC));
		cards.add(new AbilityCard(BendingAbility.ABILITY_RAVINE));
		this.scroll = 0;
	}
	
	@Override
	public void initGui() {
		this.res = new ScaledResolution(mc);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		for (int i = 0; i < cards.size(); i++) {
			cards.get(i).render(res, i, scroll);
		}
		
	}
	
	@Override
	public void updateScreen() {
		if (Mouse.isButtonDown(0)) {
			if (!wasMouseDown) {
				wasMouseDown = true;
				startScroll = getMouseScroll();
				System.out.println("start @ " + startScroll);
			}
			
			// scroll += Mouse.getDX();
			progressScroll += getMouseScroll() - startScroll;
			startScroll = getMouseScroll();
			
		} else {
			if (wasMouseDown) {
				scroll = progressScroll;
			}
			wasMouseDown = false;
		}
	}
	
	private int getMouseX() {
		return Mouse.getX();
	}
	
	public int getMouseScroll() {
		return scroll + getMouseX();
	}
	
}
