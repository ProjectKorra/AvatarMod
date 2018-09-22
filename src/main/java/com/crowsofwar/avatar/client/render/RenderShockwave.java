package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RenderShockwave extends Render<EntityShockwave> {

	public RenderShockwave(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityShockwave entity, double x, double y, double z, float entityYaw, float partialTicks) {
		World world = entity.world;
		for (int j = 0; j < 360/entity.getParticleAmount(); j++) {
			Vector lookPos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
						j * entity.getParticleAmount()), 0).times(0.5);
			world.spawnParticle(entity.getParticle(), entity.posX, entity.getEntityBoundingBox().minY + 1.5, entity.posZ, lookPos.x() * (entity.getSpeed() * entity.ticksExisted/2),
					lookPos.y(), lookPos.z() * (entity.getSpeed() * entity.ticksExisted/2));
		}

	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityShockwave entity) {
		return null;
	}

}

