package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.gorecore.util.BackedVector;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityWave extends Entity {
	
	private final Vector internalVelocity;
	private final Vector internalPosition;
	
	public EntityWave(World world) {
		super(world);
		//@formatter:off
		this.internalVelocity = new BackedVector(x -> this.motionX = x / 20, y -> this.motionY = y / 20, z -> this.motionZ = z / 20,
				() -> this.motionX * 20, () -> this.motionY * 20, () -> this.motionZ * 20);
		this.internalPosition = new Vector();
	}
	
	@Override
	public void onUpdate() {
		
		Vector move = velocity();
		Vector newPos = getVecPosition().add(move);
		setPosition(newPos.x(), newPos.y(), newPos.z());
		
		if (ticksExisted > 7000 || worldObj.getBlockState(getPosition()).getBlock() != Blocks.WATER) setDead();
		
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
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
	
}
