package com.maxandnoah.avatar.client.gui;

import com.maxandnoah.avatar.common.gui.IAvatarGui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class RadialMenu extends GuiScreen implements IAvatarGui {
	
	private static final ResourceLocation radialMenu = new ResourceLocation("avatarmod", "textures/gui/radial_segment.png");
	
	@Override
	public void initGui() {
		super.initGui();
		System.out.println("EEE");
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);
		System.out.println("aa");
		
		mc.getTextureManager().bindTexture(radialMenu);
		drawTexturedModalRect(0, 0, 0, 0, 141, 129);
	}
	
}
