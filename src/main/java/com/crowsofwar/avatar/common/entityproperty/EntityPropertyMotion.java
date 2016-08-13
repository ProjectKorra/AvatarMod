package com.crowsofwar.avatar.common.entityproperty;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

/**
 * An entity property which allows simple access to the entity's motion vector by manipulating the
 * motionX, motionY, and motionZ fields. All methods for velocity are in m/s.
 *
 */
public class EntityPropertyMotion implements IEntityProperty<Vec3> {
	
	private final Vec3 internalVelocity;
	private final Entity entity;
	
	public EntityPropertyMotion(Entity entity) {
		this.internalVelocity = Vec3.createVectorHelper(0, 0, 0);
		this.entity = entity;
	}
	
	@Override
	public Vec3 getValue() {
		internalVelocity.xCoord = entity.motionX * 20;
		internalVelocity.yCoord = entity.motionY * 20;
		internalVelocity.zCoord = entity.motionZ * 20;
		return internalVelocity;
	}
	
	@Override
	public void setValue(Vec3 value) {
		entity.motionX = value.xCoord / 20;
		entity.motionY = value.yCoord / 20;
		entity.motionZ = value.zCoord / 20;
	}
	
}
