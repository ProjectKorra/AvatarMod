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

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.common.entity.EntityAirblade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class RenderAirblade extends Render<EntityAirblade> {
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/airblade.png");
	private static final Random random = new Random();
	
	public RenderAirblade(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityAirblade entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		
		Matrix4f mat = new Matrix4f();
		mat.translate((float) x, (float) y + .1f, (float) z);
		mat.rotate(entity.ticksExisted / 3f, 0, 1, 0);
		
		//@formatter:off
		float n = -.75f, p = .75f;
		Vector4f nw = new Vector4f(n, 0, n, 1).mul(mat);
		Vector4f ne = new Vector4f(p, 0, n, 1).mul(mat);
		Vector4f sw = new Vector4f(n, 0, p, 1).mul(mat);
		Vector4f se = new Vector4f(p, 0, p, 1).mul(mat);
		//@formatter:on
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		drawQuad(2, nw, ne, se, sw, 0, 0, 1, 1);
		GlStateManager.enableLighting();
		
		if (entity.ticksExisted % 3 == 0) {
			World world = entity.worldObj;
			AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
			double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
			double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
			double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
			world.spawnParticle(EnumParticleTypes.CLOUD, spawnX, spawnY, spawnZ, 0, 0, 0);
		}
		
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
	protected ResourceLocation getEntityTexture(EntityAirblade entity) {
		return null;
	}
	
}
