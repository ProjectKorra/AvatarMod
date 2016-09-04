package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityFlames extends Entity implements IPhysics {
	
	private final IEntityProperty<Vector> propVelocity;
	
	/**
	 * @param worldIn
	 */
	public EntityFlames(World worldIn) {
		super(worldIn);
		this.propVelocity = new EntityPropertyMotion(this);
		setSize(0.1f, 0.1f);
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Support saving/loading of EntityFlames
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		setDead();
	}
	
	@Override
	public void onUpdate() {
		Vector velocityPerTick = getVelocity().dividedBy(20);
		moveEntity(velocityPerTick.x(), velocityPerTick.y(), velocityPerTick.z());
	}
	
	@Override
	public Vector getVecPosition() {
		return Vector.getEntityPos(this);
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
		propVelocity.getValue().add(vel);
	}
	
}
