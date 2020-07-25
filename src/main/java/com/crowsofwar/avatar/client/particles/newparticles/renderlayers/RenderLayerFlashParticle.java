package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class RenderLayerFlashParticle extends RenderLayer {
	
	public static final RenderLayerFlashParticle INSTANCE = new RenderLayerFlashParticle();
	
	private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("minecraft", "textures/particle/particles.png");
	
	@Override
	public void preRenderParticles() {
		if(CLIENT_CONFIG.particleSettings.releaseShaderOnFlashParticleRender && GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != 0) {
			GL20.glUseProgram(0);
		}
		if(CLIENT_CONFIG.particleSettings.voxelFlashParticles) {
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(PARTICLE_TEXTURES);
		super.preRenderParticles();
	}
	
	@Override
	public void postRenderParticles() {
		super.postRenderParticles();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}
	
}
