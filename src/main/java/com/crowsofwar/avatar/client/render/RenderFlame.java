package com.crowsofwar.avatar.client.render;

import java.util.Random;

import com.crowsofwar.avatar.common.entity.EntityFlame;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderFlame extends Render {
	
	private static final Random random = new Random();
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float interpolatedYaw, float p_76986_9_) {
		
		EntityFlame flame = (EntityFlame) entity;
		int time = flame.ticksExisted % 5; // TODO Incorporate more randomization into particle
											// spawning
		
		// TODO [1.10] Figure out how to spawn particles
		// if (time == 1) flame.worldObj.spawnParticle("smoke", flame.posX, flame.posY, flame.posZ,
		// random.nextGaussian() * 0.1, 0.07,
		// random.nextGaussian() * 0.1);
		// if (time == 3) flame.worldObj.spawnParticle("smoke", flame.posX, flame.posY, flame.posZ,
		// random.nextGaussian() * 0.07, 0.1,
		// random.nextGaussian() * 0.07);
		// if (time == 4) flame.worldObj.spawnParticle("flame", flame.posX, flame.posY, flame.posZ,
		// random.nextGaussian() * 0.03, 0.1,
		// random.nextGaussian() * 0.03);
		
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		// Don't need to implement - only called from bindEntityTexture, which is optional and only
		// called from doRender.
		return null;
	}
	
}
