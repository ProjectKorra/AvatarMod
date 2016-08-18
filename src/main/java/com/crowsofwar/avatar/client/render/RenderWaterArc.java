package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;

import net.minecraft.util.ResourceLocation;

public class RenderWaterArc extends RenderArc {
	
	private static final ResourceLocation water = new ResourceLocation("avatarmod", "textures/entity/water-ribbon.png");
	
	@Override
	protected ResourceLocation getTexture() {
		return water;
	}
	
	@Override
	protected void onDrawSegment(EntityArc arc, EntityControlPoint first, EntityControlPoint second) {
		// Parametric equation
		
		// TODO [1.10] Find out how to spawn particles, and find a good vector class
		// Vec3d from = Vec3d.createVectorHelper(0, 0, 0);
		// Vec3d to = minus(second.getPosition(), first.getPosition());
		// Vec3d diff = minus(to, from);
		// Vec3d offset = first.getPosition();
		// Vec3d direction = copy(diff);
		// direction.normalize();
		// Vec3d spawnAt = plus(offset, times(direction, Math.random()));
		// Vec3d velocity = first.getVelocity();
		// arc.worldObj.spawnParticle("splash", spawnAt.xCoord, spawnAt.yCoord, spawnAt.zCoord,
		// velocity.xCoord, velocity.yCoord,
		// velocity.zCoord);
	}
	
}
