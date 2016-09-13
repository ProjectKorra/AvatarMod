package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityWave extends Entity implements IPhysics {
	
	private final IEntityProperty<Vector> propVelocity;
	private final Vector internalPosition;
	
	public EntityWave(World world) {
		super(world);
		this.propVelocity = new EntityPropertyMotion(this);
		this.internalPosition = new Vector();
	}

	@Override
	public void onUpdate() {
		
	}
	
	@Override
	public Vector getVecPosition() {
		return internalPosition.set(posX, posY, posZ);
	}

	@Override
	public Vector getVelocity() {
		return propVelocity.getValue();
	}

	@Override
	public void setVelocity(Vector vel) {
		propVelocity.setValue(vel);
	}

	@Override
	public void addVelocity(Vector vel) {
		setVelocity(getVelocity().add(vel));
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
