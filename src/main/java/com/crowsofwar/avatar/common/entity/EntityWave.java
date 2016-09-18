package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.gorecore.util.BackedVector;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityWave extends Entity {
	
	private final Vector internalVelocity;
	private final Vector internalPosition;
	
	public EntityWave(World world) {
		super(world);
		//@formatter:off
		this.internalVelocity = new BackedVector(x -> motionX = x * 20, y -> motionY = y * 20, z -> motionZ = z * 20,
				() -> motionX / 20, () -> motionY / 20, () -> motionZ / 20);
		this.internalPosition = new Vector();
	}
	
	@Override
	public void onUpdate() {
		
		Vector move = velocity();
		Vector newPos = getVecPosition().add(move);
		setPosition(newPos.x(), newPos.y(), newPos.z());
		
//		System.out.println("Velocity is: " + velocity());
		System.out.println("MotionX is: " + motionX);
		
		if (ticksExisted > 70) setDead();
		
	}
	
	public Vector getVecPosition() {
		return internalPosition.set(posX, posY, posZ);
	}
	
	/**
	 * Get velocity in m/s. Any modifications to this vector will modify the actual velocity.
	 */
	public Vector velocity() {
		return internalVelocity;
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
		// TODO Save/load waves??
		setDead();
	}
	
}
