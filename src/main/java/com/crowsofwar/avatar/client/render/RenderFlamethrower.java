package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityFlamethrower;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class RenderFlamethrower extends Render<EntityFlamethrower> {

	//Although this is bad practice, mobs won't show particles otherwise.

	public RenderFlamethrower(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityFlamethrower entity, double x, double y, double z, float entityYaw, float partialTicks) {

		World world = entity.world;
		if (world.isRemote) {
			for (double i = 0; i < Math.max(Math.min((int) (1 / entity.getAvgSize()), 2), 1); i++) {
				Random random = new Random();
				AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
				double spawnX = boundingBox.minX + random.nextDouble() / 15 * (boundingBox.maxX - boundingBox.minX);
				double spawnY = boundingBox.minY + random.nextDouble() / 15 * (boundingBox.maxY - boundingBox.minY);
				double spawnZ = boundingBox.minZ + random.nextDouble() / 15 * (boundingBox.maxZ - boundingBox.minZ);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(255, 10, 5)
						.scale(entity.getAvgSize() * 3F).element(entity.getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 30), 10)
						.scale(entity.getAvgSize() * 3F).element(entity.getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).clr(255, 10, 5)
						.scale(entity.getAvgSize() * 2F).element(entity.getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 30), 10)
						.scale(entity.getAvgSize() * 2F).element(entity.getElement()).collide(true).spawn(world);
			}
		}
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityFlamethrower entity) {
		return null;
	}
}
