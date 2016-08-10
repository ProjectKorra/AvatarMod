package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFlame extends Entity implements IPhysics {
	
	private static final int DATAWATCHER_VELX = 2, DATAWATCHER_VELY = 3, DATAWATCHER_VELZ = 4,
			DATAWATCHER_DIST_TRAVELLED = 5;
	
	private static final int MAX_DIST_TRAVELLED = 10;
	
	/**
	 * Don't access this- used internally by {@link #getVelocity()}
	 */
	private Vec3 internalVelocity;
	
	private Vec3 source;
	
	public EntityFlame(World world) {
		super(world);
		setSize(0.25f, 0.25f);
		internalVelocity = Vec3.createVectorHelper(0, 0, 0);
	}
	
	public EntityFlame(World world, double posX, double posY, double posZ) {
		this(world);
		setPosition(posX, posY, posZ);
	}
	
	public EntityFlame(World world, double posX, double posY, double posZ, double mx, double my, double mz) {
		this(world, posX, posY, posZ);
		setVelocity(Vec3.createVectorHelper(mx, my, mz));
	}
	
	@Override
	protected void entityInit() {
		dataWatcher.addObject(DATAWATCHER_VELX, 0f);
		dataWatcher.addObject(DATAWATCHER_VELY, 0f);
		dataWatcher.addObject(DATAWATCHER_VELZ, 0f);
		dataWatcher.addObject(DATAWATCHER_DIST_TRAVELLED, 0f);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		setDead();
		// TODO Support flame saving and loading
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		Vec3 velocity = getVelocity();
		moveEntity(velocity.xCoord / 20, velocity.yCoord / 20, velocity.zCoord / 20);
		float size = (float) (Math.pow(getDistanceTravelled() / MAX_DIST_TRAVELLED, 1.7) * 1.5);
		setSize(size, size);
		setDistanceTravelled();
		if (isCollided) {
			int x = (int) Math.floor(posX);
			int y = (int) Math.floor(posY);
			int z = (int) Math.floor(posZ);
			if (!worldObj.isRemote && worldObj.getBlock(x, y, z) == Blocks.air) worldObj.setBlock(x, y, z, Blocks.fire);
		}
		if (isCollided || getDistanceTravelled() > 10) setDead();
	}
	
	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);
		source = Vec3.createVectorHelper(x, y, z);
	}
	
	/**
	 * Get the distance from where the flame originated.
	 */
	public float getDistanceTravelled() {
		return dataWatcher.getWatchableObjectFloat(DATAWATCHER_DIST_TRAVELLED);
	}
	
	public void setDistanceTravelled() {
		if (!worldObj.isRemote) {
			float dist = (float) Vec3.createVectorHelper(posX, posY, posZ).distanceTo(source);
			dataWatcher.updateObject(DATAWATCHER_DIST_TRAVELLED, dist);
		}
	}
	
	@Override
	public Vec3 getVelocity() {
		internalVelocity.xCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELX);
		internalVelocity.yCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELY);
		internalVelocity.zCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELZ);
		return internalVelocity;
	}
	
	@Override
	public Vec3 getPosition() {
		return VectorUtils.getEntityPos(this);
	}
	
	@Override
	public void setVelocity(Vec3 vel) {
		if (!worldObj.isRemote) {
			dataWatcher.updateObject(DATAWATCHER_VELX, (float) vel.xCoord);
			dataWatcher.updateObject(DATAWATCHER_VELY, (float) vel.yCoord);
			dataWatcher.updateObject(DATAWATCHER_VELZ, (float) vel.zCoord);
		}
	}
	
	@Override
	public void addVelocity(Vec3 vel) {
		setVelocity(VectorUtils.plus(getVelocity(), vel));
	}
	
}
