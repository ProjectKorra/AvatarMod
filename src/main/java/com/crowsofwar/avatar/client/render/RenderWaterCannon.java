package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityWaterCannon;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class RenderWaterCannon extends RenderArc {
	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/water-ribbon.png");

	private final ParticleSpawner particleSpawner;

	public RenderWaterCannon(RenderManager renderManager) {
		super(renderManager, true);
		enableFullBrightness();
		particleSpawner = new ClientParticleSpawner();
	}

	@Override
	public void doRender(Entity entity, double xx, double yy, double zz, float p_76986_8_,
						 float partialTicks) {

		EntityWaterCannon cannon = (EntityWaterCannon) entity;
		renderArc(cannon, partialTicks, 3f, 3f * cannon.getSizeMultiplier());
		for (int degree = 0; degree < 360; degree++) {
			double radians = Math.toRadians(degree);
			double x = Math.cos(radians);
			double z = Math.sin(radians);
			cannon.world.spawnParticle(EnumParticleTypes.CLOUD, x + cannon.getOwner().posX, cannon.getOwner().posY,
					z + cannon.getOwner().posZ, 0, 0, 0);
		}
		/*float Angle = 2 % 360;
		double cos = Math.cos(Math.toRadians(Angle));
		double sin = Math.sin(Math.toRadians(Angle));
		double x = 2 * cos - 2 * sin;
		double y = 2 * cos + 2 * sin;
		entity.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, x, y, z,
				((EntityWaterCannon) entity).velocity().x(), ((EntityWaterCannon) entity).velocity().y(), ((EntityWaterCannon) entity).velocity().z());**/

	}


	@Override
	protected ResourceLocation getTexture() {
		return TEXTURE;
	}

}


