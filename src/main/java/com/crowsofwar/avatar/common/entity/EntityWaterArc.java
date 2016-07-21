package com.crowsofwar.avatar.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityWaterArc extends EntityArc {
	
	private static final Vec3 GRAVITY = Vec3.createVectorHelper(0, -9.81 / 20, 0);
	
	public EntityWaterArc(World world) {
		super(world);
	}
	
	@Override
	protected void onCollision(Entity entity) {
		entity.setVelocity(0, 0.1, 0);
	}
	
	@Override
	protected void onCollideWithBlock() {
		
	}
	
	@Override
	protected Vec3 getGravityVector() {
		return GRAVITY;
	}
	
	public static EntityWaterArc findFromId(World world, int id) {
		for (Object obj : world.loadedEntityList) {
			if (obj instanceof EntityWaterArc && ((EntityWaterArc) obj).getId() == id) return (EntityWaterArc) obj;
		}
		return null;
	}
	
}
