package com.crowsofwar.avatar.common.entity;

import javax.vecmath.Vector3d;

import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFlame extends Entity implements IPhysics {

	private static final int DATAWATCHER_VELX = 2,
			DATAWATCHER_VELY = 3, DATAWATCHER_VELZ = 4;
	
	/**
	 * Don't access this- used internally by {@link #getVelocity()}
	 */
	private Vec3 internalVelocity;
	
	public EntityFlame(World world) {
		super(world);
		internalVelocity = Vec3.createVectorHelper(0, 0, 0);
	}
	
	public EntityFlame(World world, double posX, double posY, double posZ) {
		this(world);
		setPosition(posX, posY, posZ);
	}
	
	public EntityFlame(World world, double posX, double posY, double posZ, double mx, double my, double mz) {
		this(world, posX, posY, posZ);
		setVelocity(mx, my, mz);
	}
	
	@Override
	protected void entityInit() {
		dataWatcher.addObject(DATAWATCHER_VELX, 0f);
		dataWatcher.addObject(DATAWATCHER_VELY, 0f);
		dataWatcher.addObject(DATAWATCHER_VELZ, 0f);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		setDead();
		// TODO Support flame saving and loading
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		Vec3 velocity = getVelocity();
		moveEntity(velocity.xCoord / 20, velocity.yCoord / 20, velocity.zCoord / 20);
		if (isCollided || ticksExisted >= 100) setDead();
	}

	@Override
	public Vec3 getVelocity() {
		internalVelocity.xCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELX);
		internalVelocity.yCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELY);
		internalVelocity.zCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELZ);
		return internalVelocity;
	}

	@Override
	public Vec3 getPosition() {
		return VectorUtils.getEntityPos(this);
	}

	@Override
	public void setVelocity(Vec3 vel) {
		if (!worldObj.isRemote) {
			dataWatcher.updateObject(DATAWATCHER_VELX, (float) vel.xCoord);
			dataWatcher.updateObject(DATAWATCHER_VELY, (float) vel.yCoord);
			dataWatcher.updateObject(DATAWATCHER_VELZ, (float) vel.zCoord);
		}
	}
	
}
