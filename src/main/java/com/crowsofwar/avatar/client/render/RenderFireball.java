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

import static net.minecraft.client.renderer.GlStateManager.*;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.common.entity.EntityFireball;

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
public class RenderFireball extends Render<EntityFireball> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/fireball.png");
	
	public RenderFireball(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityFireball entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		
		float rotation = entity.ticksExisted / 5f;
		
		GlStateManager.enableBlend();
		
		pushMatrix();
		renderCube((float) x, (float) y, (float) z, //
				0, 8 / 256.0, 0, 8 / 256.0, //
				.5f, //
				0, entity.ticksExisted / 25f, 0);
		popMatrix();
		
		disableLighting();
		
		color(1, 1, 1, .5f);
		renderCube((float) x, (float) y, (float) z, //
				8 / 256.0, 16 / 256.0, 0 / 256.0, 8 / 256.0, //
				.8f, //
				rotation * .2f, rotation, rotation * -.4f);
		color(1, 1, 1, 1);
		
		enableLighting();
		
	}
	
	private void renderCube(float x, float y, float z, double u1, double u2, double v1, double v2, float size,
			float rotateX, float rotateY, float rotateZ) {
		Matrix4f mat = new Matrix4f();
		mat.translate(x, y + .4f, z);
		
		mat.rotate(rotateX, 1, 0, 0);
		mat.rotate(rotateY, 0, 1, 0);
		mat.rotate(rotateZ, 0, 0, 1);
		
		// @formatter:off
		// Can't use .mul(size) here because it would mul the w component
		Vector4f
		lbf = new Vector4f(-.5f*size, -.5f*size, -.5f*size, 1).mul(mat),
		rbf = new Vector4f(0.5f*size, -.5f*size, -.5f*size, 1).mul(mat),
		ltf = new Vector4f(-.5f*size, 0.5f*size, -.5f*size, 1).mul(mat),
		rtf = new Vector4f(0.5f*size, 0.5f*size, -.5f*size, 1).mul(mat),
		lbb = new Vector4f(-.5f*size, -.5f*size, 0.5f*size, 1).mul(mat),
		rbb = new Vector4f(0.5f*size, -.5f*size, 0.5f*size, 1).mul(mat),
		ltb = new Vector4f(-.5f*size, 0.5f*size, 0.5f*size, 1).mul(mat),
		rtb = new Vector4f(0.5f*size, 0.5f*size, 0.5f*size, 1).mul(mat);
		
		// @formatter:on
		
		drawQuad(2, ltb, lbb, lbf, ltf, u1, v1, u2, v2); // -x
		drawQuad(2, rtb, rbb, rbf, rtf, u1, v1, u2, v2); // +x
		drawQuad(2, rbb, rbf, lbf, lbb, u1, v1, u2, v2); // -y
		drawQuad(2, rtb, rtf, ltf, ltb, u1, v1, u2, v2); // +y
		drawQuad(2, rtf, rbf, lbf, ltf, u1, v1, u2, v2); // -z
		drawQuad(2, rtb, rbb, lbb, ltb, u1, v1, u2, v2); // +z
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
	protected ResourceLocation getEntityTexture(EntityFireball entity) {
		return null;
	}
	
}
