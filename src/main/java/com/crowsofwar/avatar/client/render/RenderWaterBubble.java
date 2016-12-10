/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.client.render;

import static net.minecraft.util.math.MathHelper.cos;
import static net.minecraft.util.math.MathHelper.sin;

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
		
		float colorEnhancement = 1.2f;
		
		Minecraft mc = Minecraft.getMinecraft();
		mc.renderEngine.bindTexture(water);
		GlStateManager.enableBlend();
		GlStateManager.color(colorEnhancement, colorEnhancement, colorEnhancement, 0.7f);
		
		Matrix4f mat = new Matrix4f();
		mat.translate((float) x - 0.5f, (float) y, (float) z - 0.5f);
		
		// (0 Left)/(1 Right), (0 Bottom)/(1 Top), (0 Front)/(1 Back)
		
		Vector4f mid = new Vector4f((float) x, (float) y + .5f, (float) z, 1);
		
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
		
		float t1 = bubble.ticksExisted * (float) Math.PI / 10f;
		float t2 = t1 + (float) Math.PI / 2f;
		float amt = .05f;
		
		lbf.add(cos(t1)*amt, sin(t2)*amt, cos(t2)*amt, 0);
		rbf.add(sin(t1)*amt, cos(t2)*amt, sin(t2)*amt, 0);
		lbb.add(sin(t2)*amt, cos(t2)*amt, cos(t2)*amt, 0);
		rbb.add(cos(t2)*amt, cos(t1)*amt, cos(t1)*amt, 0);
		
		ltf.add(cos(t2)*amt, cos(t1)*amt, sin(t1)*amt, 0);
		rtf.add(sin(t2)*amt, sin(t1)*amt, cos(t1)*amt, 0);
		ltb.add(sin(t1)*amt, sin(t2)*amt, cos(t1)*amt, 0);
		rtb.add(cos(t1)*amt, cos(t2)*amt, sin(t1)*amt, 0);
		
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
		
		GlStateManager.color(1, 1, 1, 1);
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
