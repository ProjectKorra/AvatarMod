package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.gorecore.util.BackedVector;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class AvatarEntity extends Entity {
	
	private final Vector internalVelocity;
	private final Vector internalPosition;
	
	/**
	 * @param world
	 */
	public AvatarEntity(World world) {
		super(world);
		this.internalVelocity = createInternalVelocity();
		this.internalPosition = new BackedVector(x -> this.posX = x, y -> this.posY = y, z -> this.posZ = z,
				() -> posX, () -> posY, () -> posZ);
	}
	
	/**
	 * Get the velocity of this entity in m/s. Changes to this vector will be reflected in the
	 * entity's actual velocity.
	 */
	public Vector velocity() {
		return internalVelocity;
	}
	
	/**
	 * Get the position of this entity. Changes to this vector will be reflected in the entity's
	 * actual position.
	 */
	public Vector position() {
		return internalPosition;
	}
	
	//@formatter:off
	protected Vector createInternalVelocity() {
		return new BackedVector(
				x -> this.motionX = x / 20,
				y -> this.motionY = y / 20,
				z -> this.motionZ = z / 20,
				() -> this.motionX * 20,
				() -> this.motionY * 20,
				() -> this.motionZ * 20);
	}
		
}
