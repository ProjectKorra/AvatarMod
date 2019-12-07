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
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author CrowsOfWar
 */
public class RenderFlames extends Render<EntityFlames> {

	private final Random random;
	private final ParticleSpawner particleSpawner;

	/**
	 * @param renderManager
	 */
	public RenderFlames(RenderManager renderManager, ParticleSpawner particle) {
		super(renderManager);
		this.random = new Random();
		this.particleSpawner = particle;
	}

	@Override
	public void doRender(EntityFlames entity, double x, double y, double z, float entityYaw,
						 float partialTicks) {

		particleSpawner.spawnParticles(entity.world, AvatarParticles.getParticleFlames(), 4, 6,
				Vector.getEntityPos(entity), new Vector(0.02, 0.01, 0.02), true);

		//Bad practice, but it works with mobs and servers.
		World world = entity.world;
		if (world.isRemote) {
			for (double i = 0; i < entity.width; i += 0.05) {
				Random random = new Random();
				AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
				double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
				double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
				double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(12).clr(255, 10, 5)
						.scale(entity.getAvgSize() * 4).element(entity.getElement()).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(12).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 60), 10)
						.scale(entity.getAvgSize() * 4).element(entity.getElement()).spawn(world);
			}
		}

		// entity.world.spawnParticle(AvatarParticles.getParticleFlames(),
		// entity.posX,
		// entity.posY,
		// entity.posZ,
		// // entity.world.spawnParticle(EnumParticleTypes.FLAME,
		// entity.posX, entity.posY,
		// // entity.posZ,
		// (random.nextGaussian() - 0.5) * 0.02, random.nextGaussian() * 0.01,
		// (random.nextGaussian() - 0.5) * 0.02);

	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFlames entity) {
		return null;
	}

}
