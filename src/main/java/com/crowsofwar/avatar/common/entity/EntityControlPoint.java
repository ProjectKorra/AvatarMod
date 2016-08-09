package com.crowsofwar.avatar.common.entity;



import java.util.List;

import com.crowsofwar.avatar.common.bending.FirebendingState;
import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityControlPoint extends Entity implements IPhysics {
	
	private static final int DATAWATCHER_VELOCITY = 3; // 3,4,5
	private static final int DATAWATCHER_ID = 6;
	
	private static int nextId = 1;
	
	protected EntityArc arc;
	protected EntityPlayer owner;
	
	private Vec3 internalPosition;
	private Vec3 internalVelocity;
	
	public EntityControlPoint(World world) {
		super(world);
		internalPosition = Vec3.createVectorHelper(0, 0, 0);
		internalVelocity = Vec3.createVectorHelper(0, 0, 0);
		if (!worldObj.isRemote) setId(nextId++);
		System.out.println("Spawned CP via vanilla with Id " + getId());
	}
	
	public EntityControlPoint(EntityArc arc, float size, double x, double y, double z) {
		super(arc.worldObj);
		this.arc = arc;
		setSize(size, size);
		internalPosition = Vec3.createVectorHelper(x, y, z);
		internalVelocity = Vec3.createVectorHelper(0, 0, 0);
		if (!worldObj.isRemote) setId(nextId++);
		System.out.println("Spawned CP via custom with Id " + getId());
	}
	
	@Override
	protected void entityInit() {
		dataWatcher.addObject(DATAWATCHER_VELOCITY, 0f);
		dataWatcher.addObject(DATAWATCHER_VELOCITY + 1, 0f);
		dataWatcher.addObject(DATAWATCHER_VELOCITY + 2, 0f);
		dataWatcher.addObject(DATAWATCHER_ID, 0);
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
		if (!collisions.isEmpty()) {
			Entity collidedWith = collisions.get(0);
			if (!(collidedWith instanceof EntityControlPoint) && collidedWith != this.arc) {
//				System.out.println(collisions.get(0));
				onCollision(collisions.get(0));
			}
		}
		
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
		motionX = vel.xCoord;
		motionY = vel.yCoord;
		motionZ = vel.zCoord;
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
	
	public EntityPlayer getOwner() {
		return owner;
	}
	
	public void setOwner(EntityPlayer owner) {
		this.owner = owner;
	}
	
	/**
	 * Get the Id of this control point. It is unique until the ControlPoint despawns.
	 * The Id is synced between server and client.
	 */
	public int getId() {
		return dataWatcher.getWatchableObjectInt(DATAWATCHER_ID);
	}
	
	/**
	 * Synchronize the Id between server and client.
	 */
	public void setId(int id) {
		dataWatcher.updateObject(DATAWATCHER_ID, id);
	}
	
	/**
	 * "Attach" the arc to this control point, meaning that the control
	 * point now has a reference to the given arc.
	 */
	public void setArc(EntityArc arc) {
		this.arc = arc;
	}
	
	public static EntityControlPoint findFromId(World world, int id) {
		for (Object obj : world.loadedEntityList) {
			if (obj instanceof EntityControlPoint && ((EntityControlPoint) obj).getId() == id) return (EntityControlPoint) obj;
		}
		return null;
	}
	
}
