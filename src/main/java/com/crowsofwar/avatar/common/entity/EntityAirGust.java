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

public class EntityAirGust extends EntityArc {
	
	public static final Vec3 ZERO = Vec3.createVectorHelper(0, 0, 0);
	
	public EntityAirGust(World world) {
		super(world);
		setSize(0.5f, 0.5f);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
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

	@Override
	protected void onCollideWithBlock() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Vec3 getGravityVector() {
		return ZERO;
	}
	
}
