package com.crowsofwar.avatar.common.entity;

import net.minecraft.util.Vec3;

/**
 * Describes an entity which can be moved by physics. sort of.
 *
 */
public interface IPhysics {
	
	Vec3 getPosition();
	Vec3 getVelocity();
	void setVelocity(Vec3 vel);
	
}
