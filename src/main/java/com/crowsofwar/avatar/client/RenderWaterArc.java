package com.crowsofwar.avatar.client;

import static com.crowsofwar.avatar.common.util.VectorUtils.*;

import com.crowsofwar.avatar.client.particles.AvatarParticles;
import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityArc.ControlPoint;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderWaterArc extends RenderArc {

	private static final ResourceLocation fire = new ResourceLocation("avatarmod", "textures/entity/fire-ribbon.png");
	
	@Override
	protected ResourceLocation getTexture() {
		return fire;
	}

	@Override
	protected void onDrawSegment(EntityArc arc, ControlPoint first, ControlPoint second) {
		// Parametric equation
		Vec3 from = Vec3.createVectorHelper(0, 0, 0);
		Vec3 to = minus(second.getPosition(), first.getPosition());
		Vec3 diff = minus(to, from);
		Vec3 offset = first.getPosition();
		Vec3 direction = copy(diff);
		direction.normalize();
		Vec3 spawnAt = plus(offset, times(direction, Math.random()));
		Vec3 velocity = first.getVelocity();
		arc.worldObj.spawnParticle("splash", spawnAt.xCoord, spawnAt.yCoord, spawnAt.zCoord, velocity.xCoord,
				velocity.yCoord, velocity.zCoord);
	}

}
