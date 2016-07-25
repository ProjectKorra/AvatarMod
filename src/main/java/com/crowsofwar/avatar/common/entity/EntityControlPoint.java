package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityControlPoint extends Entity implements IPhysics {
	
	private static final int DATAWATCHER_VELOCITY = 3;
	
	private Vec3 internalPosition;
	private Vec3 internalVelocity;
	
	public EntityControlPoint(World world, double x, double y, double z) {
		super(world);
		internalPosition = Vec3.createVectorHelper(x, y, z);
		internalVelocity = Vec3.createVectorHelper(0, 0, 0);
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}
	
	public void setPosition(Vec3 pos) {
		setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
	}
	
	public void move(double x, double y, double z) {
		posX += x;
		posY += y;
		posZ += z;
	}
	
	public void move(Vec3 offset) {
		move(offset.xCoord, offset.yCoord, offset.zCoord);
	}
	
	public double getXPos() {
		return getPosition().xCoord;
	}
	
	public double getYPos() {
		return getPosition().yCoord;
	}
	
	public double getZPos() {
		return getPosition().zCoord;
	}
	
	public double getDistance(EntityControlPoint point) {
		return getPosition().distanceTo(point.getPosition());
	}

	@Override
	public Vec3 getPosition() {
		internalPosition.xCoord = posX;
		internalPosition.yCoord = posY;
		internalPosition.zCoord = posZ;
		return internalPosition;
	}

	@Override
	public Vec3 getVelocity() {
		internalVelocity.xCoord = dataWatcher.getWatchableObjectInt(DATAWATCHER_VELOCITY);
		internalVelocity.yCoord = dataWatcher.getWatchableObjectInt(DATAWATCHER_VELOCITY + 1);
		internalVelocity.zCoord = dataWatcher.getWatchableObjectInt(DATAWATCHER_VELOCITY + 2);
		return internalVelocity;
	}

	@Override
	public void setVelocity(Vec3 vel) {
		if (!worldObj.isRemote) {
			dataWatcher.updateObject(DATAWATCHER_VELOCITY, vel.xCoord);
			dataWatcher.updateObject(DATAWATCHER_VELOCITY + 1, vel.yCoord);
			dataWatcher.updateObject(DATAWATCHER_VELOCITY + 2, vel.zCoord);
		}
	}

	@Override
	public void addVelocity(Vec3 vel) {
		setVelocity(VectorUtils.plus(getVelocity(), vel));
	}

}
