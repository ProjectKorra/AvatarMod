package com.crowsofwar.avatar.common.entity;

import java.util.List;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyDatawatcher;
import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.EntityPropertyVector;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.avatar.common.util.VectorUtils;

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
		return new AirGustControlPoint(this, size, 0, 0, 0);
	}
	
	@Override
	protected int getAmountOfControlPoints() {
		return 2;
	}
	
	@Override
	protected double getControlPointMaxDistanceSq() {
		return 16; // 4 * 4
	}
	
	@Override
	protected double getControlPointTeleportDistanceSq() {
		return 64; // 8 * 8
	}
	
	public class AirGustControlPoint extends EntityControlPoint {
		
		public AirGustControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}
		
		@Override
		protected void onCollision(Entity entity) {
			entity.addVelocity(entity.posX - this.posX, 0.2, entity.posZ - this.posZ);
		}
		
	}
	
}
