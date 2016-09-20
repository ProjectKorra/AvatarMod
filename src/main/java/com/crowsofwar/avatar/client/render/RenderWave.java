package com.crowsofwar.avatar.client.render;

import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.common.entity.EntityWave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class RenderWave extends Render<EntityWave> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft",
			"textures/blocks/brick.png");
	
	/**
	 * @param renderManager
	 */
	public RenderWave(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityWave entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		
		Tessellator t = Tessellator.getInstance();
		VertexBuffer vb = t.getBuffer();
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		
		// Gui
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(x, y, z).tex(0, 1).endVertex();
		vb.pos(x, y + 1, z).tex(0, 0).endVertex();
		vb.pos(x + 1, y + 1, z).tex(1, 0).endVertex();
		vb.pos(x + 1, y, z).tex(1, 1).endVertex();
		
		t.draw();
		
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityWave entity) {
		return null;
	}
	
}
