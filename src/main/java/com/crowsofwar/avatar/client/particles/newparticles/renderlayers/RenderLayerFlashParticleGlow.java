package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;

public class RenderLayerFlashParticleGlow extends RenderLayerFlashParticle {

	public static final RenderLayerFlashParticleGlow INSTANCE = new RenderLayerFlashParticleGlow();
	
	@Override
	public void preRenderParticles() {
//		if(CLIENT_CONFIG.particleSettings.releaseShaderOnFlashParticleRender && GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != 0) {
//			GL20.glUseProgram(0);
//		}
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		super.preRenderParticles();
	}
}
