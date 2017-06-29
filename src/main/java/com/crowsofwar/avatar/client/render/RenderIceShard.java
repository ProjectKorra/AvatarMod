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

import org.joml.Matrix4f;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.common.entity.EntityIceShard;
import com.crowsofwar.gorecore.util.Vector;

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
public class RenderIceShard extends Render<EntityIceShard> {
	
	/**
	 * @param renderManager
	 */
	protected RenderIceShard(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void doRender(EntityIceShard entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		
		Matrix4f mat = new Matrix4f();
		Vector4d spike1Top = new Vector4d(0, 0, -3, 1).mul(mat);
		
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityIceShard entity) {
		return null;
	}
	
	private void drawQuad(int normal, Vector pos1, Vector pos2, Vector pos3, Vector pos4, double u1,
			double v1, double u2, double v2) {
		
		Tessellator t = Tessellator.getInstance();
		VertexBuffer vb = t.getBuffer();
		
		if (normal == 0 || normal == 2) {
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.x(), pos1.y(), pos1.z()).tex(u2, v1).endVertex();
			vb.pos(pos2.x(), pos2.y(), pos2.z()).tex(u2, v2).endVertex();
			vb.pos(pos3.x(), pos3.y(), pos3.z()).tex(u1, v2).endVertex();
			vb.pos(pos4.x(), pos4.y(), pos4.z()).tex(u1, v1).endVertex();
			t.draw();
		}
		if (normal == 1 || normal == 2) {
			
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.x(), pos1.y(), pos1.z()).tex(u2, v1).endVertex();
			vb.pos(pos4.x(), pos4.y(), pos4.z()).tex(u1, v1).endVertex();
			vb.pos(pos3.x(), pos3.y(), pos3.z()).tex(u1, v2).endVertex();
			vb.pos(pos2.x(), pos2.y(), pos2.z()).tex(u2, v2).endVertex();
			t.draw();
		}
	}
	
	private void drawQuad(int normal, Vector4d pos1, Vector4d pos2, Vector4d pos3, Vector4d pos4, double u1,
			double v1, double u2, double v2) {
		drawQuad(normal, new Vector(pos1.x, pos1.y, pos1.z), new Vector(pos2.x, pos2.y, pos2.z),
				new Vector(pos3.x, pos3.y, pos3.z), new Vector(pos4.x, pos4.y, pos4.z), u1, v1, u2, v2);
	}
	
}
