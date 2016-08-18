package com.crowsofwar.gorecore.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class GoreCoreRenderTickEvent {
	
	private EntityRenderer gcRenderer;
	
	@SubscribeEvent
	public void renderTick(RenderTickEvent event) {
		if (event.phase == Phase.START) {
			Minecraft mc = Minecraft.getMinecraft();
			if (gcRenderer == null) gcRenderer = new GoreCoreEntityRenderer(mc, mc.getResourceManager());
			if (mc.entityRenderer != gcRenderer) mc.entityRenderer = gcRenderer;
		}
	}
	
}
