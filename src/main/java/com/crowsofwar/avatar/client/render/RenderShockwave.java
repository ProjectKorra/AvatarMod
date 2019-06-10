package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderShockwave extends Render<EntityShockwave> {

	public RenderShockwave(RenderManager renderManager) {
		super(renderManager);
	}


	@Override
	public void doRender(EntityShockwave entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		EnumParticleTypes particle;
		if (EnumParticleTypes.getByName(entity.getParticleName()) != null) {
			particle = EnumParticleTypes.getByName(entity.getParticleName());
		}
		else {
			particle = AvatarParticles.getParticleFromName(entity.getParticleName());
		}
		for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (entity.getRange() * 15 / entity.getParticleAmount())) {
			double x2 = entity.posX + (entity.ticksExisted * entity.getSpeed()) * Math.sin(angle);
			double y2 = entity.posY + 0.5;
			double z2 = entity.posZ + (entity.ticksExisted * entity.getSpeed()) * Math.cos(angle);
			Vector speed = new Vector((entity.ticksExisted * entity.getSpeed()) * Math.sin(angle) * (entity.getParticleSpeed() * 10),
					entity.getParticleSpeed() / 2, (entity.ticksExisted * entity.getSpeed()) * Math.cos(angle) * (entity.getParticleSpeed() * 10));
			entity.world.spawnParticle(particle, x2, y2, z2, speed.x(), speed.y(), speed.z());
		}

		if (entity.getSphere()) {
			double x1, y1, z1, xVel, yVel, zVel;
			for (double theta = 0; theta <= 180; theta += 1) {
				double dphi = entity.getParticleController() / Math.sin(Math.toRadians(theta));
				for (double phi = 0; phi < 360; phi += dphi) {
					double rphi = Math.toRadians(phi);
					double rtheta = Math.toRadians(theta);

					x1 = entity.ticksExisted * entity.getSpeed() * Math.cos(rphi) * Math.sin(rtheta);
					y1 = entity.ticksExisted * entity.getSpeed() * Math.sin(rphi) * Math.sin(rtheta);
					z1 = entity.ticksExisted * entity.getSpeed() * Math.cos(rtheta);
					xVel = x1 * entity.getParticleSpeed() * 10;
					yVel = y1 * entity.getParticleSpeed() * 10;
					zVel = z1 * entity.getParticleSpeed() * 10;

					entity.world.spawnParticle(particle, x1 + entity.posX,
							y1 + entity.posY, z1 + entity.posZ, xVel, yVel, zVel);

				}
			}//Creates a sphere. Courtesy of Project Korra's Air Burst!
		}
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityShockwave entity) {
		return null;
	}
}
