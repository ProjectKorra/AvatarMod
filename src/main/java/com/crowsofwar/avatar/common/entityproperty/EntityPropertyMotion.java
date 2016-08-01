package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class EntityPropertyMotion implements IEntityProperty<Vec3> {
	
	private final Vec3 internalVelocity;
	private final Entity entity;
	
	public EntityPropertyMotion(Entity entity) {
		this.internalVelocity = Vec3.createVectorHelper(0, 0, 0);
		this.entity = entity;
	}
	
	@Override
	public Vec3 getValue() {
		internalVelocity.xCoord = entity.motionX;
		internalVelocity.yCoord = entity.motionY;
		internalVelocity.zCoord = entity.motionZ;
		return internalVelocity;
	}
	
	@Override
	public void setValue(Vec3 value) {
		entity.motionX = value.xCoord;
		entity.motionY = value.yCoord;
		entity.motionZ = value.zCoord;
	}
	
}
