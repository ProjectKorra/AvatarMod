package com.crowsofwar.avatar.common.entity;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityWaterArc extends EntityArc {
	
	private static final Vec3 GRAVITY = Vec3.createVectorHelper(0, -9.81 / 20, 0);
	
	public EntityWaterArc(World world) {
		super(world);
	}
	
	@Override
	protected void onCollideWithBlock() {
		
		if (worldObj.isRemote) {
			Random random = new Random();
			
			double xVel = 0, yVel = 0, zVel = 0;
			double offX = 0, offY = 0, offZ = 0;
			
			if (isCollidedVertically) {
				
				xVel = 5;
				yVel = 3.5;
				zVel = 5;
				offX = 0;
				offY = 0.6;
				offZ = 0;
				
			} else {
				
				xVel = 7;
				yVel = 2;
				zVel = 7;
				offX = 0.6;
				offY = 0.2;
				offZ = 0.6;
				
			}
			
			xVel *= 0.0;
			yVel *= 0.0;
			zVel *= 0.0;
			
			int particles = random.nextInt(3) + 4;
			for (int i = 0; i < particles; i++) {
				
				worldObj.spawnParticle("splash", 
						posX + random.nextGaussian() * offX,
						posY + random.nextGaussian() * offY + 0.2,
						posZ + random.nextGaussian() * offZ,
						random.nextGaussian() * xVel,
						random.nextGaussian() * yVel,
						random.nextGaussian() * zVel);
				
			}
			
		}
		
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
