package com.crowsofwar.avatar.common.entity;



import java.util.List;

import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityControlPoint extends Entity implements IPhysics {
	
	private static final int DATAWATCHER_VELOCITY = 3;
	
	protected final EntityArc arc;
	
	private Vec3 internalPosition;
	private Vec3 internalVelocity;
	
	public EntityControlPoint(EntityArc arc, float size, double x, double y, double z) {
		super(arc.worldObj);
		this.arc = arc;
		setSize(size, size);
		internalPosition = Vec3.createVectorHelper(x, y, z);
		internalVelocity = Vec3.createVectorHelper(0, 0, 0);
	}
	
	@Override
	protected void entityInit() {
		dataWatcher.addObject(DATAWATCHER_VELOCITY, 0f);
		dataWatcher.addObject(DATAWATCHER_VELOCITY + 1, 0f);
		dataWatcher.addObject(DATAWATCHER_VELOCITY + 2, 0f);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		setPosition(VectorUtils.plus(getPosition(), getVelocity()));
		setVelocity(VectorUtils.times(getVelocity(), 0.4));
		
		List<Entity> collisions = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox);
		if (!collisions.isEmpty() && collisions.get(0) != this.arc) onCollision(collisions.get(0));
		
	}
	
	/**
	 * Called whenever the control point is in contact with this entity.
	 */
	protected void onCollision(Entity entity) {}
	
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
		internalVelocity.xCoord = motionX;
		internalVelocity.yCoord = motionY;
		internalVelocity.zCoord = motionZ;
		return internalVelocity;
	}

	@Override
	public void setVelocity(Vec3 vel) {
		setVelocity(vel.xCoord, vel.yCoord, vel.zCoord); // TODO - temporary solution, @SideOnly Client.
	}

	@Override
	public void addVelocity(Vec3 vel) {
		setVelocity(VectorUtils.plus(getVelocity(), vel));
	}
	
	/**
	 * Get the arc that this control point belongs to.
	 * @return
	 */
	public EntityArc getArc() {
		return arc;
	}

}
