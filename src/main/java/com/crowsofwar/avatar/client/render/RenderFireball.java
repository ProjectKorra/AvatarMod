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

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Random;

import static com.crowsofwar.avatar.client.render.RenderUtils.drawQuad;
import static com.crowsofwar.avatar.common.util.AvatarParticleUtils.rotateAroundAxisX;
import static com.crowsofwar.avatar.common.util.AvatarParticleUtils.rotateAroundAxisY;
import static java.lang.Math.sin;
import static net.minecraft.client.renderer.GlStateManager.*;
import static net.minecraft.util.math.MathHelper.cos;

/**
 * @author CrowsOfWar
 */
public class RenderFireball extends Render<EntityFireball> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/fireball.png");
	private static final Random random = new Random();

	public RenderFireball(RenderManager renderManager) {
		super(renderManager);
	}

	// @formatter:off
	@Override
	public void doRender(EntityFireball entity, double xx, double yy, double zz, float entityYaw,
						 float partialTicks) {


		float x = (float) xx, y = (float) yy, z = (float) zz;

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

		float ticks = entity.ticksExisted + partialTicks;

		float rotation = ticks / 3f;
		float size = .8f + cos(ticks / 5f) * .05f;
		size *= Math.sqrt(entity.getSize() / 30f);


		enableBlend();

		if (entity.ticksExisted % 3 == 0) {
			World world = entity.world;
			AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
			double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
			double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
			double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
			world.spawnParticle(EnumParticleTypes.FLAME, spawnX, spawnY, spawnZ, 0, 0, 0);
			//I'm using 0.03125, because that results in a size of 0.5F when rendering, as the default size for the fireball is actually 16.
			//This is due to weird rendering shenanigans
			int particles = 30 * (int) (entity.getSize() * 0.03125);
			for (int angle = 0; angle < particles; angle++) {
				Vec3d direction = Vec3d.ZERO;
				Vec3d position = entity.getPositionVector();
				Vec3d entitySpeed = new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
				Vec3d particleSpeed = new Vec3d(0.1, 0.05, 0.1).scale(entity.getSize() * 0.03125);
				double angle2 = world.rand.nextDouble() * Math.PI * 2;
				double radius = (angle / (particles / (entity.getSize() * 0.03125F)));
				double x1 = radius * Math.cos(angle);
				double y1 = angle / (particles / (1 + entity.getSize() * 0.03125));
				double z1 = radius * sin(angle);
				double speed = world.rand.nextDouble() * 2 + 1;
				double omega = Math.signum(speed * ((Math.PI * 2) / 20 - speed / (20 * radius)));
				angle2 += omega;
				Vec3d pos = new Vec3d(x1, y1, z1);
				Vec3d pVel = new Vec3d(particleSpeed.x * radius * omega * Math.cos(angle2), particleSpeed.y, particleSpeed.z * radius * omega * sin(angle2));
				pVel = rotateAroundAxisX(pVel, entity.rotationPitch - 90);
				pVel = rotateAroundAxisY(pVel, entity.rotationYaw);
				pos = rotateAroundAxisX(pos, entity.rotationPitch + 90);
				pos = rotateAroundAxisY(pos, entity.rotationYaw);
				world.spawnParticle(AvatarParticles.getParticleFlames(), true, pos.x + position.x + direction.x, pos.y + position.y + direction.y,
						pos.z + position.z + direction.z, pVel.x + entitySpeed.x, pVel.y + entitySpeed.y, pVel.z + entitySpeed.z);
			}
			/*AvatarParticleUtils.spawnSpinningDirectionalVortex(world, entity.getOwner(), Vec3d.ZERO, particles,
					3, 0.001, particles / (entity.getSize() * 0.03125), AvatarParticles.getParticleFlames(),
					entity.getPositionVector(), new Vec3d(0.1, 0.05, 0.01).scale(entity.getSize() * 0.03125),
					new Vec3d(entity.motionX, entity.motionY, entity.motionZ));**/
		}

		//   if (MinecraftForgeClient.getRenderPass() == 0) {
		disableLighting();

		renderCube(x, y, z, //
				0, 8 / 256.0, 0, 8 / 256.0, //
				.5f, //
				ticks / 25F, ticks / 25f, ticks / 25F);

		int i = 15728880;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);

		//  } else {

		pushMatrix();
		renderCube(x, y, z, //
				8 / 256.0, 16 / 256.0, 0 / 256.0, 8 / 256.0, //
				size, //
				rotation * .2f, rotation, rotation * -.4f);
		popMatrix();

		//  }
		enableLighting();
		disableBlend();

	}
	// @formatter:on

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
				lbf = new Vector4f(-.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				rbf = new Vector4f(0.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				ltf = new Vector4f(-.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				rtf = new Vector4f(0.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				lbb = new Vector4f(-.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				rbb = new Vector4f(0.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				ltb = new Vector4f(-.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat),
				rtb = new Vector4f(0.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat);

		// @formatter:on

		drawQuad(2, ltb, lbb, lbf, ltf, u1, v1, u2, v2); // -x
		drawQuad(2, rtb, rbb, rbf, rtf, u1, v1, u2, v2); // +x
		drawQuad(2, rbb, rbf, lbf, lbb, u1, v1, u2, v2); // -y
		drawQuad(2, rtb, rtf, ltf, ltb, u1, v1, u2, v2); // +y
		drawQuad(2, rtf, rbf, lbf, ltf, u1, v1, u2, v2); // -z
		drawQuad(2, rtb, rbb, lbb, ltb, u1, v1, u2, v2); // +z
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFireball entity) {
		return TEXTURE;
	}

}
