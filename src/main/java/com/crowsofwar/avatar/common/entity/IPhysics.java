package com.crowsofwar.avatar.common.entity;

import net.minecraft.util.Vec3;

/**
 * Describes an entity which can be moved by physics. sort of.
 *
 */
public interface IPhysics {
	
	/**
	 * Get the position in world coordinates.
	 */
	Vec3 getPosition();
	
	/**
	 * Get the velocity in m/s.
	 */
	Vec3 getVelocity();
	
	/**
	 * Set the velocity
	 * @param vel Velocity in m/s
	 */
	void setVelocity(Vec3 vel);
	
	/**
	 * Add velocity to the object, ignoring mass.
	 * @param vel Velocity in m/s
	 */
	void addVelocity(Vec3 vel);
	
}
