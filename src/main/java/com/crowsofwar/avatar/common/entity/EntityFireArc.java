package com.crowsofwar.avatar.common.entity;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityFireArc extends EntityArc {
	
	private static final Vec3d GRAVITY = Vec3d.createVectorHelper(0, -9.81 / 60, 0);
	
	public EntityFireArc(World world) {
		super(world);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (inWater) {
			setDead();
			Random random = new Random();
			if (worldObj.isRemote) {
				int particles = random.nextInt(3) + 4;
				for (int i = 0; i < particles; i++) {
					worldObj.spawnParticle("cloud", posX, posY, posZ, (random.nextGaussian() - 0.5) * 0.05 + motionX / 10,
							random.nextGaussian() * 0.08, (random.nextGaussian() - 0.5) * 0.05 + motionZ / 10);
				}
			}
			worldObj.playSoundAtEntity(this, "random.fizz", 1.0f, random.nextFloat() * 0.3f + 1.1f);// BlockFire
		}
	}
	
	public static EntityFireArc findFromId(World world, int id) {
		return (EntityFireArc) EntityArc.findFromId(world, id);
	}
	
	@Override
	protected void onCollideWithBlock() {
		if (!worldObj.isRemote) {
			int x = (int) Math.floor(posX);
			int y = (int) Math.floor(posY);
			int z = (int) Math.floor(posZ);
			worldObj.setBlock(x, y, z, Blocks.fire);
		}
	}
	
	@Override
	protected Vec3d getGravityVector() {
		return GRAVITY;
	}
	
	@Override
	public EntityControlPoint createControlPoint(float size) {
		return new FireControlPoint(this, size, 0, 0, 0);
	}
	
	public static class FireControlPoint extends EntityControlPoint {
		
		public FireControlPoint(World world) {
			super(world);
		}
		
		public FireControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}
		
		@Override
		protected void onCollision(Entity entity) {
			entity.setFire(3);
			arc.setDead();
		}
		
	}
	
}
