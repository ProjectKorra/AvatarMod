package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.gorecore.GoreCore;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAirGust extends EntityArc {
	
	public static final Vec3 ZERO = Vec3.createVectorHelper(0, 0, 0);
	
	public EntityAirGust(World world) {
		super(world);
		setSize(0.5f, 0.5f);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		EntityControlPoint first = getControlPoint(0);
		EntityControlPoint second = getControlPoint(1);
		if (first.getPosition().squareDistanceTo(second.getPosition()) >= getControlPointMaxDistanceSq()) {
			setDead();
		}
		if (ticksExisted > 80) setDead();
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	protected void onCollideWithBlock() {
		
	}
	
	@Override
	protected Vec3 getGravityVector() {
		return ZERO;
	}
	
	@Override
	protected EntityControlPoint createControlPoint(float size) {
		return new AirGustControlPoint(this, 0.1f, 0, 0, 0);
	}
	
	@Override
	public int getAmountOfControlPoints() {
		return 2;
	}
	
	@Override
	protected double getControlPointMaxDistanceSq() {
		return 100; // 10
	}
	
	@Override
	protected double getControlPointTeleportDistanceSq() {
		// Note: Is not actually called.
		// Set dead as soon as reached sq-distance
		return 200;
	}
	
	public static class AirGustControlPoint extends EntityControlPoint {
		
		public AirGustControlPoint(World world) {
			super(world);
		}
		
		public AirGustControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}
		
		@Override
		protected void onCollision(Entity entity) {
			if (entity != owner && entity != GoreCore.proxy.getClientSidePlayer()) {
				double multiplier = 10;
				entity.addVelocity((entity.posX - this.posX) * multiplier, 0.4, (entity.posZ - this.posZ) * multiplier);
				setDead();
			}
		}
		
		@Override
		public void onUpdate() {
			if (arc.getControlPoint(0) == this) {
				float expansionRate = 1f / 20;
				setSize(width + expansionRate, width + expansionRate);
			}
		}
		
	}
	
}
