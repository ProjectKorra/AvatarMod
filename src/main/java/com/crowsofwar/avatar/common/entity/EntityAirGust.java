package com.crowsofwar.avatar.common.entity;

import java.util.List;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyDatawatcher;
import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.EntityPropertyVector;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAirGust extends Entity implements IPhysics {
	
	public static int DATAWATCHER_MOTION = 2;
	
	private final Vec3 internalPosition;
	private final EntityPropertyDatawatcher<Vec3> internalVelocity;
	
	public EntityAirGust(World world) {
		super(world);
		setSize(0.5f, 0.5f);
		this.internalPosition = Vec3.createVectorHelper(0, 0, 0);
		this.internalVelocity = new EntityPropertyVector(this, dataWatcher, DATAWATCHER_MOTION);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (ticksExisted % 5 == 0) internalVelocity.sync();
		Vec3 velocity = getVelocity();
		moveEntity(velocity.xCoord, velocity.yCoord, velocity.zCoord);
		if (isCollided || ticksExisted > 30) setDead();
		
		List<Entity> collidedWith = worldObj.getEntitiesWithinAABB(Entity.class, boundingBox);
		if (!collidedWith.isEmpty()) {
			Entity collided = collidedWith.get(0); // Only bother collision-checking 1 entity
			collided.addVelocity(velocity.xCoord * 3, 0.3, velocity.zCoord * 3);
		}
		
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
		return VectorUtils.times(internalVelocity.getValue(), 0.05);
	}
	
	@Override
	public void setVelocity(Vec3 vel) {
		internalVelocity.setValue(vel);
	}
	
	@Override
	public void addVelocity(Vec3 vel) {
		internalVelocity.setValue(getVelocity().addVector(vel.xCoord, vel.yCoord, vel.zCoord));
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}
	
}
