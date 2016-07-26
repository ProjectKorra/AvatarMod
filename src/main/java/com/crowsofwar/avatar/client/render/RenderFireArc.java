package com.crowsofwar.avatar.client.render;

import static com.crowsofwar.avatar.common.util.VectorUtils.copy;
import static com.crowsofwar.avatar.common.util.VectorUtils.minus;
import static com.crowsofwar.avatar.common.util.VectorUtils.plus;
import static com.crowsofwar.avatar.common.util.VectorUtils.times;

import com.crowsofwar.avatar.client.particles.AvatarParticles;
import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderFireArc extends RenderArc {

	private static final ResourceLocation fire = new ResourceLocation("avatarmod", "textures/entity/fire-ribbon.png");
	
	@Override
	protected ResourceLocation getTexture() {
		return fire;
	}

	@Override
	protected void onDrawSegment(EntityArc arc, EntityControlPoint first, EntityControlPoint second) {
		// Parametric equation
		Vec3 from = Vec3.createVectorHelper(0, 0, 0);
		Vec3 to = minus(second.getPosition(), first.getPosition());
		Vec3 diff = minus(to, from);
		Vec3 offset = first.getPosition();
		Vec3 direction = copy(diff);
		direction.normalize();
		Vec3 spawnAt = plus(offset, times(direction, Math.random()));
		Vec3 velocity = first.getVelocity();
		AvatarParticles.createParticle(arc.worldObj, spawnAt.xCoord, spawnAt.yCoord, spawnAt.zCoord, velocity.xCoord / 20,
				0.05, velocity.zCoord / 20);
	}

}
