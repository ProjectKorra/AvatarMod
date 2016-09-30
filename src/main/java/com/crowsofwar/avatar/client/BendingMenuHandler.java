package com.crowsofwar.avatar.client;

import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.client.gui.RadialMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
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
public class BendingMenuHandler extends Gui {
	
	private final RadialMenu gui;
	private final Minecraft mc;
	// FIXME Need to recalculate upon screen resize?
	private ScaledResolution resolution;
	
	public BendingMenuHandler() {
		gui = new RadialMenu(1);
		mc = Minecraft.getMinecraft();
		resolution = new ScaledResolution(mc);
		System.out.println("Constructed BMH");
	}
	
	@SubscribeEvent
	public void onGuiRender(RenderGameOverlayEvent.Post e) {
		int mouseX = Mouse.getX() * resolution.getScaledWidth() / mc.displayWidth;
		int mouseY = Mouse.getY() * resolution.getScaledHeight() / mc.displayHeight;
		
		drawTexturedModalRect(100, 100, 0, 0, 100, 100);
		// gui.drawScreen(mouseX, mouseY, 0);
		
		System.out.println("rrender " + e);
	}
	
}
