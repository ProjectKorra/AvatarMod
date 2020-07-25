package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureMap;

public class RenderLayerAdditive extends RenderLayer {

	public static final RenderLayerAdditive INSTANCE = new RenderLayerAdditive();
	
	@Override
	public void preRenderParticles() {
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		super.preRenderParticles();
	}
	
	@Override
	public void postRenderParticles() {
		super.postRenderParticles();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
	}
}
