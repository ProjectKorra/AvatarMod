package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RenderAirGust extends Render<EntityAirGust> {

	public RenderAirGust(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityAirGust entity, double x, double y, double z, float entityYaw, float partialTicks) {
		World world = entity.world;
		if (world.isRemote) {
			for (int i = 0; i < 4; i++) {
				AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
				double spawnX = boundingBox.getCenter().x + world.rand.nextGaussian() / 10;
				double spawnY = boundingBox.getCenter().y + world.rand.nextGaussian() / 10;
				double spawnZ = boundingBox.getCenter().z + world.rand.nextGaussian() / 10;
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45,
						world.rand.nextGaussian() / 45).time(4).clr(0.85F, 0.85F, 0.85F)
						.scale(entity.getAvgSize() * 1.25F).element(entity.getElement()).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45,
						world.rand.nextGaussian() / 45).time(12).clr(0.85F, 0.85F, 0.85F)
						.scale(entity.getAvgSize() * 1.25F).element(entity.getElement()).spawn(world);
			}
		}
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityAirGust entity) {
		return null;
	}
}
