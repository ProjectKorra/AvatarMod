package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.gorecore.util.VectorD;

/**
 * Describes an entity which can be moved by physics. sort of.
 *
 */
public interface IPhysics {
	
	/**
	 * Get the position in world coordinates.
	 */
	VectorD getVecPosition();
	
	/**
	 * Get the velocity in m/s.
	 */
	VectorD getVelocity();
	
	/**
	 * Set the velocity
	 * 
	 * @param vel
	 *            Velocity in m/s
	 */
	void setVelocity(VectorD vel);
	
	/**
	 * Add velocity to the object, ignoring mass.
	 * 
	 * @param vel
	 *            Velocity in m/s
	 */
	void addVelocity(VectorD vel);
	
}
