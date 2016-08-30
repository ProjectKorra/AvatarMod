package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityRavine extends Entity implements IPhysics {
	
	private final IEntityProperty<Vector> propVelocity;
	private Vector initialPosition;
	
	/**
	 * @param world
	 */
	public EntityRavine(World world) {
		super(world);
		
		this.propVelocity = new EntityPropertyMotion(this);
		
	}
	
	public double getSqrDistanceTravelled() {
		return getVecPosition().sqrDist(initialPosition);
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	public void onEntityUpdate() {
		
		if (initialPosition == null) {
			initialPosition = getVecPosition();
		}
		
		Vector position = getVecPosition();
		Vector velocity = getVelocity();
		
		Vector nowPos = position.add(velocity.times(0.05));
		setPosition(nowPos.x(), nowPos.y(), nowPos.z());
		
		if (getSqrDistanceTravelled() > 100) setDead();
		if (!worldObj.getBlockState(getPosition().offset(EnumFacing.DOWN)).isNormalCube()) setDead();
		
	}
	
	@Override
	public Vector getVecPosition() {
		return new Vector(this);
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
		setVelocity(getVelocity().plus(vel));
	}
	
}
