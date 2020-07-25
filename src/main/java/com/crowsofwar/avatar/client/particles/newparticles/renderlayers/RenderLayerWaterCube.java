package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderLayerWaterCube extends RenderLayer {

	public static final RenderLayerWaterCube INSTANCE = new RenderLayerWaterCube();

	private static final ResourceLocation WATER = new ResourceLocation("minecraft", "textures/blocks/water_still.png");
	
	@Override
	public void preRenderParticles() {
		Minecraft.getMinecraft().renderEngine.bindTexture(WATER);
		Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	}
}
