package com.crowsofwar.avatar.common.entity;

import net.minecraft.util.math.Vec3d;

/**
 * Describes an entity which can be moved by physics. sort of.
 *
 */
public interface IPhysics {
	
	/**
	 * Get the position in world coordinates.
	 */
	Vec3d getPhysicsPosition();
	
	/**
	 * Get the velocity in m/s.
	 */
	Vec3d getVelocity();
	
	/**
	 * Set the velocity
	 * 
	 * @param vel
	 *            Velocity in m/s
	 */
	void setVelocity(Vec3d vel);
	
	/**
	 * Add velocity to the object, ignoring mass.
	 * 
	 * @param vel
	 *            Velocity in m/s
	 */
	void addVelocity(Vec3d vel);
	
}
