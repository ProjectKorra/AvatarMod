package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAirGust extends Entity implements IPhysics {
	
	private final Vec3 internalPosition;
	private final IEntityProperty<Vec3> internalVelocity;
	
	public EntityAirGust(World world) {
		super(world);
		this.internalPosition = Vec3.createVectorHelper(0, 0, 0);
		this.internalVelocity = new EntityPropertyMotion(this);
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
		return internalVelocity.getValue();
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
		
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}
	
}
