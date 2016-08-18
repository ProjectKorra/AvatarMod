package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;

import net.minecraft.util.ResourceLocation;

public class RenderFireArc extends RenderArc {
	
	private static final ResourceLocation fire = new ResourceLocation("avatarmod", "textures/entity/fire-ribbon.png");
	
	public RenderFireArc() {
		enableFullBrightness();
	}
	
	@Override
	protected ResourceLocation getTexture() {
		return fire;
	}
	
	@Override
	protected void onDrawSegment(EntityArc arc, EntityControlPoint first, EntityControlPoint second) {
		// Parametric equation
		// For parameters, they will be same as linear equation: y = mx+b
		
		// TODO [1.10] Find a good vector class to use- Vec3d no longer exists
		// Vec3d m = VectorUtils.minus(second.getPosition(), first.getPosition());
		// Vec3d b = first.getPosition();
		// double x = Math.random(); // 0-1
		// Vec3d spawnAt = VectorUtils.plus(VectorUtils.times(m, x), b);
		// Vec3d velocity = Vec3d.createVectorHelper(0, 0, 0);
		//
		// AvatarParticles.createParticle(arc.worldObj, spawnAt.xCoord, spawnAt.yCoord,
		// spawnAt.zCoord, velocity.xCoord / 20, 0.05,
		// velocity.zCoord / 20);
	}
	
}
