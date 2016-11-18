package com.crowsofwar.avatar.client.gui;

import static net.minecraft.client.renderer.GlStateManager.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;

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
	private int startScroll, startX;
	
	private boolean wasMouseDown;
	
	public SkillsGui() {
		this.cards = new ArrayList<>();
		cards.add(new AbilityCard(BendingAbility.ABILITY_AIR_JUMP));
		cards.add(new AbilityCard(BendingAbility.ABILITY_FIRE_ARC));
		cards.add(new AbilityCard(BendingAbility.ABILITY_RAVINE));
		this.scroll = 0;
	}
	
	public SkillsGui(BendingController controller) {
		this.cards = new ArrayList<>();
		for (BendingAbility ability : controller.getAllAbilities()) {
			cards.add(new AbilityCard(ability));
		}
	}
	
	@Override
	public void initGui() {
		this.res = new ScaledResolution(mc);
		System.out.println("INIT");
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		// Draw Gui Background
		pushMatrix();
		scale(1f / res.getScaleFactor(), 1f / res.getScaleFactor(), 1);
		translate(-mc.displayWidth / 2 + scroll / 4, -mc.displayHeight / 2, 0);
		scale(mc.displayWidth / 256f, mc.displayHeight / 256f, 1);
		scale(2, 2, 1);
		mc.renderEngine.bindTexture(AvatarUiTextures.waterBg);
		drawTexturedModalRect(0, 0, 256, 256, width, height);
		popMatrix();
		
		for (int i = 0; i < cards.size(); i++) {
			cards.get(i).render(res, i, scroll);
		}
		
	}
	
	@Override
	public void updateScreen() {
		if (Mouse.isButtonDown(0)) {
			if (!wasMouseDown) {
				wasMouseDown = true;
				startScroll = scroll;
				startX = getMouseX();
			}
			
			// scroll += Mouse.getDX();
			scroll = startScroll + getMouseX() - startX;
			
		} else {
			wasMouseDown = false;
		}
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		scroll += Mouse.getDWheel() / 3;
	}
	
	private int getMouseX() {
		return Mouse.getX();
	}
	
	public int getMouseScroll() {
		return scroll + getMouseX();
	}
	
}
