package com.crowsofwar.avatar.client.render;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.common.entity.EntityWaterBubble;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
public class RenderWaterBubble extends Render<EntityWaterBubble> {
	
	private static final ResourceLocation water = new ResourceLocation("minecraft",
			"textures/blocks/water_still.png");
	
	public RenderWaterBubble(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityWaterBubble bubble, double x, double y, double z, float entityYaw,
			float partialTicks) {
		
		Minecraft mc = Minecraft.getMinecraft();
		mc.renderEngine.bindTexture(water);
		GlStateManager.enableBlend();
		
		Matrix4f mat = new Matrix4f();
		mat.translate((float) x - 0.5f, (float) y, (float) z - 0.5f);
		
		// (0 Left)/(1 Right), (0 Bottom)/(1 Top), (0 Front)/(1 Back)
		
		// @formatter:off
		Vector4f
		lbf = new Vector4f(0, 0, 0, 1).mul(mat),
		rbf = new Vector4f(1, 0, 0, 1).mul(mat),
		ltf = new Vector4f(0, 1, 0, 1).mul(mat),
		rtf = new Vector4f(1, 1, 0, 1).mul(mat),
		lbb = new Vector4f(0, 0, 1, 1).mul(mat),
		rbb = new Vector4f(1, 0, 1, 1).mul(mat),
		ltb = new Vector4f(0, 1, 1, 1).mul(mat),
		rtb = new Vector4f(1, 1, 1, 1).mul(mat);
		// @formatter:on
		
		float existed = bubble.ticksExisted / 4f;
		int anim = (int) ((int) existed % 16);
		float v1 = anim / 16f, v2 = v1 + 1f / 16;
		
		drawQuad(1, ltb, lbb, lbf, ltf, 0, v1, 1, v2); // -x
		drawQuad(0, rtb, rbb, rbf, rtf, 0, v1, 1, v2); // +x
		drawQuad(1, rbb, rbf, lbf, lbb, 0, v1, 1, v2); // -y
		drawQuad(0, rtb, rtf, ltf, ltb, 0, v1, 1, v2); // +y
		drawQuad(0, rtf, rbf, lbf, ltf, 0, v1, 1, v2); // -z
		drawQuad(1, rtb, rbb, lbb, ltb, 0, v1, 1, v2); // +z
		
		GlStateManager.disableBlend();
		
	}
	
	private void drawQuad(int normal, Vector4f pos1, Vector4f pos2, Vector4f pos3, Vector4f pos4, double u1,
			double v1, double u2, double v2) {
		
		Tessellator t = Tessellator.getInstance();
		VertexBuffer vb = t.getBuffer();
		
		if (normal == 0 || normal == 2) {
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).endVertex();
			vb.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).endVertex();
			vb.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).endVertex();
			vb.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).endVertex();
			t.draw();
		}
		if (normal == 1 || normal == 2) {
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).endVertex();
			vb.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).endVertex();
			vb.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).endVertex();
			vb.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).endVertex();
			t.draw();
		}
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityWaterBubble entity) {
		return null;
	}
	
}
