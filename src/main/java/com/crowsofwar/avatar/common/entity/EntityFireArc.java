package com.crowsofwar.avatar.common.entity;

import java.util.Random;

import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFireArc extends EntityArc {
	
	private static final Vector GRAVITY = new Vector(0, -9.81 / 60, 0);
	
	private static final DataParameter<FireArcBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireArc.class, FireArcBehavior.DATA_SERIALIZER);
	
	public EntityFireArc(World world) {
		super(world);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireArcBehavior.Idle(this));
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
					worldObj.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ,
							(random.nextGaussian() - 0.5) * 0.05 + motionX / 10, random.nextGaussian() * 0.08,
							(random.nextGaussian() - 0.5) * 0.05 + motionZ / 10);
				}
			}
			// TODO [1.10] Where is "random.fizz" sound??
			worldObj.playSound(posX, posY, posZ, SoundEvents.BLOCK_ANVIL_BREAK, SoundCategory.PLAYERS, 1,
					random.nextFloat() * 0.3f + 1.1f, false);
			// worldObj.playSoundAtEntity(this, "random.fizz", 1.0f,
			// random.nextFloat() * 0.3f +
			// 1.1f);// BlockFire
		}
		getBehavior().setEntity(this);
		FireArcBehavior newBehavior = (FireArcBehavior) getBehavior().onUpdate();
		if (getBehavior() != newBehavior) setBehavior(newBehavior);
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
			worldObj.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
		}
	}
	
	@Override
	protected Vector getGravityVector() {
		return GRAVITY;
	}
	
	@Override
	public ControlPoint createControlPoint(float size) {
		return new FireControlPoint(this, size, 0, 0, 0);
	}
	
	public FireArcBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(FireArcBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
	public static class FireControlPoint extends ControlPoint {
		
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
