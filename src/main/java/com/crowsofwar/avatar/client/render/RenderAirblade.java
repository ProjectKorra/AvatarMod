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

import com.crowsofwar.avatar.common.entity.EntityAirblade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Random;

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
		
		float ticks = (entity.ticksExisted + partialTicks) / 3;
		
		Matrix4f mat = new Matrix4f();
		mat.translate((float) x, (float) y + .1f, (float) z);
		mat.rotate(ticks, 0, 1, 0);
		
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
		RenderUtils.drawQuad(2, nw, ne, se, sw, 0, 0, 1, 1);
		GlStateManager.enableLighting();
		
		if (entity.ticksExisted % 3 == 0) {
			World world = entity.world;
			AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
			double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
			double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
			double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
			world.spawnParticle(EnumParticleTypes.CLOUD, spawnX, spawnY, spawnZ, 0, 0, 0);
		}
		
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityAirblade entity) {
		return null;
	}
	
}
