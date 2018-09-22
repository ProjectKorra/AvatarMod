package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityFireShockwave;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RenderFireShockwave extends Render<EntityFireShockwave> {

	public RenderFireShockwave(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityFireShockwave entity, double x, double y, double z, float entityYaw, float partialTicks) {
		World world = entity.world;
		for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (entity.getRange() * 10 * 1.5)) {

			double spawnX = (entity.posX + ((entity.ticksExisted * entity.getSpeed()) * Math.sin(angle)));
			double spawnY = (entity.posY + 1);
			double spawnZ = (entity.posZ + ((entity.ticksExisted * entity.getSpeed())) * Math.cos(angle));
			world.spawnParticle(entity.getParticle(), spawnX, spawnY, spawnZ, (entity.posX + ((entity.ticksExisted * entity.getSpeed()) * Math.sin(angle))), 0.1, ((entity.ticksExisted/10f * entity.getSpeed())) * Math.cos(angle));

		}
	}


	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityFireShockwave entity) {
		return null;
	}
}
