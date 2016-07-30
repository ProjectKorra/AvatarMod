package com.crowsofwar.avatar.client.render;

import static com.crowsofwar.avatar.common.util.VectorUtils.copy;
import static com.crowsofwar.avatar.common.util.VectorUtils.minus;
import static com.crowsofwar.avatar.common.util.VectorUtils.plus;
import static com.crowsofwar.avatar.common.util.VectorUtils.times;

import com.crowsofwar.avatar.client.particles.AvatarParticles;
import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;
import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

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
		Vec3 m = VectorUtils.minus(second.getPosition(), first.getPosition());
		Vec3 b = first.getPosition();
		double x = Math.random(); // 0-1
		Vec3 spawnAt = VectorUtils.plus(VectorUtils.times(m, x), b);
		Vec3 velocity= Vec3.createVectorHelper(0, 0, 0);
		
//		AvatarParticles.createParticle(arc.worldObj, spawnAt.xCoord, spawnAt.yCoord, spawnAt.zCoord, velocity.xCoord / 20,
//				0.05, velocity.zCoord / 20);
	}

}
