package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class RenderWaterArc extends RenderArc {
	
	private static final ResourceLocation water = new ResourceLocation("avatarmod",
			"textures/entity/water-ribbon.png");
	
	/**
	 * @param renderManager
	 */
	public RenderWaterArc(RenderManager renderManager) {
		super(renderManager);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected ResourceLocation getTexture() {
		return water;
	}
	
	@Override
	protected void onDrawSegment(EntityArc arc, EntityControlPoint first, EntityControlPoint second) {
		// Parametric equation
		
		Vector from = new Vector(0, 0, 0);
		Vector to = second.getVecPosition().minus(first.getVecPosition());
		Vector diff = to.minus(from);
		Vector offset = first.getVecPosition();
		Vector direction = diff.copy();
		direction.normalize();
		Vector spawnAt = offset.plus(direction.times(Math.random()));
		Vector velocity = first.getVelocity();
		arc.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, spawnAt.x(), spawnAt.y(), spawnAt.z(),
				velocity.x(), velocity.y(), velocity.z());
	}
	
}
