package com.crowsofwar.avatar.common.entityproperty;

import com.crowsofwar.gorecore.util.VectorD;

import net.minecraft.entity.Entity;

/**
 * An entity property which allows simple access to the entity's motion vector by manipulating the
 * motionX, motionY, and motionZ fields. All methods for velocity are in m/s.
 *
 */
public class EntityPropertyMotion implements IEntityProperty<VectorD> {
	
	private final VectorD internalVelocity;
	private final Entity entity;
	
	public EntityPropertyMotion(Entity entity) {
		this.internalVelocity = new VectorD(0, 0, 0);
		this.entity = entity;
	}
	
	@Override
	public VectorD getValue() {
		internalVelocity.setX(entity.motionX * 20);
		internalVelocity.setY(entity.motionY * 20);
		internalVelocity.setZ(entity.motionZ * 20);
		return internalVelocity;
	}
	
	@Override
	public void setValue(VectorD value) {
		entity.motionX = value.x() / 20;
		entity.motionY = value.y() / 20;
		entity.motionZ = value.z() / 20;
	}
	
}
