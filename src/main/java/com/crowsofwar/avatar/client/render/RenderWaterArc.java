package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;

import net.minecraft.client.renderer.entity.RenderManager;
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
		
		// TODO [1.10] Find out how to spawn particles, and find a good vector class
		// Vector from = new Vector(0, 0, 0);
		// Vector to = minus(second.getPosition(), first.getPosition());
		// Vector diff = minus(to, from);
		// Vector offset = first.getPosition();
		// Vector direction = copy(diff);
		// direction.normalize();
		// Vector spawnAt = plus(offset, times(direction, Math.random()));
		// Vector velocity = first.getVelocity();
		// arc.worldObj.spawnParticle("splash", spawnAt.xCoord, spawnAt.yCoord, spawnAt.zCoord,
		// velocity.xCoord, velocity.yCoord,
		// velocity.zCoord);
	}
	
}
