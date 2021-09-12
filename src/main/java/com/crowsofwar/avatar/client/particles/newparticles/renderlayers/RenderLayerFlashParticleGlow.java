package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

public class RenderLayerFlashParticleGlow extends RenderLayerFlashParticle {

	public static final RenderLayerFlashParticleGlow INSTANCE = new RenderLayerFlashParticleGlow();
	
	@Override
	public void preRenderParticles() {
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		super.preRenderParticles();
	}
}
