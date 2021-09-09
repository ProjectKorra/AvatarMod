package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;

import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;

public class RenderLayerWaterCube extends RenderLayer {

	public static final RenderLayerWaterCube INSTANCE = new RenderLayerWaterCube();

	private static final ResourceLocation WATER = new ResourceLocation("minecraft", "textures/blocks/water_still.png");
	
	@Override
	public void preRenderParticles() {
		if(CLIENT_CONFIG.particleSettings.releaseShaderOnFlashParticleRender && GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != 0) {
			GL20.glUseProgram(0);
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(WATER);
		Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	}
}
