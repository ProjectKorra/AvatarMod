package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;

public class RenderLayerBlockSheet extends RenderLayer {

	public static final RenderLayerBlockSheet INSTANCE = new RenderLayerBlockSheet();
	
	@Override
	public void preRenderParticles() {
		GlStateManager.depthMask(true);
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		super.preRenderParticles();
	}
	
	@Override
	public void postRenderParticles() {
		super.postRenderParticles();
		GlStateManager.depthMask(false);
	}
}
