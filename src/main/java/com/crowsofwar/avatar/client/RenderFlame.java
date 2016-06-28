package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.common.entity.EntityFlame;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderFlame extends Render {

	@Override
	public void doRender(Entity entity, double x, double y, double z, float interpolatedYaw, float p_76986_9_) {
		
		EntityFlame flame = (EntityFlame) entity;
		if (flame.ticksExisted % 5 == 0)
			flame.worldObj.spawnParticle("smoke", flame.posX, flame.posY, flame.posZ, 0, 0.1, 0);
		
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		// Don't need to implement - only called from bindEntityTexture, which is optional and only called from doRender.
		return null;
	}

}
