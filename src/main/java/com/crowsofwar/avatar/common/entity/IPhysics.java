package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.gorecore.util.Vector;

/**
 * Describes an entity which can be moved by physics. sort of.
 *
 */
public interface IPhysics {
	
	/**
	 * Get the position in world coordinates.
	 */
	Vector getVecPosition();
	
	/**
	 * Get the velocity in m/s.
	 */
	Vector getVelocity();
	
	/**
	 * Set the velocity
	 * 
	 * @param vel
	 *            Velocity in m/s
	 */
	void setVelocity(Vector vel);
	
	/**
	 * Add velocity to the object, ignoring mass.
	 * 
	 * @param vel
	 *            Velocity in m/s
	 */
	void addVelocity(Vector vel);
	
}
