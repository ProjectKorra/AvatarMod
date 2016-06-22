package com.maxandnoah.avatar.common.entity;

import java.util.Random;

import com.maxandnoah.avatar.AvatarMod;
import com.maxandnoah.avatar.common.network.packets.PacketCThrownBlockVelocity;
import com.maxandnoah.avatar.common.util.AvatarUtils;
import com.maxandnoah.avatar.common.util.VectorUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFloatingBlock extends Entity {
	
	public static final Block DEFAULT_BLOCK = Blocks.stone;
	public static final int DATAWATCHER_BLOCKID = 2;
	public static final int DATAWATCHER_GRAVITY = 3;
	public static final int DATAWATCHER_FLOATINGBLOCKID = 4;
	
	public static final int DATAWATCHER_VELX = 5,
			DATAWATCHER_VELY = 6, DATAWATCHER_VELZ = 7, DATAWATCHER_FRICTION = 8,
			DATAWATCHER_LIFT = 9, DATAWATCHER_INITIAL_Y = 10;
	
	private static int nextBlockID = 0;
	
	/**
	 * Holds the current velocity. Please don't use this field directly,
	 * as it is not designed to be synced. Use {@link #getVelocity()}
	 * and {@link #setVelocity(Vec3)}.
	 */
	private final Vec3 velocity;
	
	public EntityFloatingBlock(World world) {
		super(world);
		setSize(1, 1);
		velocity = Vec3.createVectorHelper(0, 0, 0);
		setGravityEnabled(false);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			System.out.println("Constructed with ID " + nextBlockID);setID(nextBlockID++);
		}
	}
	
	public EntityFloatingBlock(World world, Block block) {
		this(world);
		setBlock(block);
	}
	
	// Called from constructor of Entity class
	@Override
	protected void entityInit() {
		dataWatcher.addObject(DATAWATCHER_BLOCKID, getNameForBlock(DEFAULT_BLOCK));
		dataWatcher.addObject(DATAWATCHER_GRAVITY, 0);
		dataWatcher.addObject(DATAWATCHER_FLOATINGBLOCKID, 0);
		
		dataWatcher.addObject(DATAWATCHER_VELX, 0f);
		dataWatcher.addObject(DATAWATCHER_VELY, 0f);
		dataWatcher.addObject(DATAWATCHER_VELZ, 0f);
		
		dataWatcher.addObject(DATAWATCHER_FRICTION, 0f);
		dataWatcher.addObject(DATAWATCHER_LIFT, -1f);
		dataWatcher.addObject(DATAWATCHER_INITIAL_Y, -1f);
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setBlock(Block.getBlockFromName(nbt.getString("Block")));
		setGravityEnabled(nbt.getBoolean("Gravity"));
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("Block", getNameForBlock(getBlock()));
		nbt.setBoolean("Gravity", isGravityEnabled());
	}

	public Block getBlock() {
		Block block = (Block) Block.blockRegistry.getObject(dataWatcher.getWatchableObjectString(DATAWATCHER_BLOCKID));
		if (block == null) block = DEFAULT_BLOCK;
		return block;
	}

	public void setBlock(Block block) {
		if (block == null) block = DEFAULT_BLOCK;
		dataWatcher.updateObject(DATAWATCHER_BLOCKID, getNameForBlock(block));
	}
	
	public boolean isGravityEnabled() {
		return dataWatcher.getWatchableObjectInt(DATAWATCHER_GRAVITY) == 1;
	}
	
	public void setGravityEnabled(boolean gravity) {
		dataWatcher.updateObject(DATAWATCHER_GRAVITY, gravity ? 1 : 0);
	}
	
	/**
	 * Get the ID of this floating block. Each instance has its own unique
	 * ID. Synced between client and server.
	 */
	public int getID() {
		return dataWatcher.getWatchableObjectInt(DATAWATCHER_FLOATINGBLOCKID);
	}
	
	public void setID(int id) {
		dataWatcher.updateObject(DATAWATCHER_FLOATINGBLOCKID, id);
	}
	
	public static EntityFloatingBlock getFromID(World world, int id) {
		for (int i = 0; i < world.loadedEntityList.size(); i++) {
			Entity e = (Entity) world.loadedEntityList.get(i);
			if (e instanceof EntityFloatingBlock && ((EntityFloatingBlock) e).getID() == id) return (EntityFloatingBlock) e;
		}
		return null;
	}
	
	private String getNameForBlock(Block block) {
		return Block.blockRegistry.getNameForObject(block);
	}
	
	public int getMetadata() {
		return 0; // TODO Implement metadata tracking into DataWatcher
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (isGravityEnabled()) {
			addForce(Vec3.createVectorHelper(0, -0.01, 0));
		}
		if (isLifting()) {
			
			float percent = getLiftPercent();
			System.out.println("Lifting " + percent + "% (" + AvatarUtils.smoothstep(getInitialY(), 2, percent) + ")");
			posY = getInitialY() + AvatarUtils.smoothstep(0, 2, percent);
			if (!worldObj.isRemote) setLiftPercent(percent + 1f/20);
			if (getLiftPercent() >= 1) setNotLifting();
			
		} else {
			posX += getVelocity().xCoord / 20;
			posY += getVelocity().yCoord / 20;
			posZ += getVelocity().zCoord / 20;
			if (!worldObj.isRemote) setVelocity(VectorUtils.times(getVelocity(), getFriction()));
		}
		if (!isLifting() && (isCollided || worldObj.getBlock((int) posX, (int) posY, (int) posZ) != Blocks.air)) {
			setDead();
			
			// Spawn particles
			Random random = new Random();
			for (int i = 0; i < 7; i++) {
				worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(getBlock()) + "_" + getMetadata(),
						posX, posY + 0.3, posZ, random.nextGaussian() * 0.1, random.nextGaussian() * 0.1, random.nextGaussian() * 0.1);
			}
			
		} 
		
	}
	
	public void addForce(Vec3 force) {
//		VectorUtils.add(velocity, force);
		setVelocity(VectorUtils.plus(getVelocity(), force));
	}
	
	public Vec3 getVelocity() {
		velocity.xCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELX);
		velocity.yCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELY);
		velocity.zCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELZ);
		return velocity;
	}
	
	public void setVelocity(Vec3 velocity) {
		if (!worldObj.isRemote) {
			dataWatcher.updateObject(DATAWATCHER_VELX, (float) velocity.xCoord);
			dataWatcher.updateObject(DATAWATCHER_VELY, (float) velocity.yCoord);
			dataWatcher.updateObject(DATAWATCHER_VELZ, (float) velocity.zCoord);
		}
	}
	
	public void syncVelocity() {
//		AvatarMod.network.sendToAllAround(new PacketCThrownBlockVelocity(this),
//				new TargetPoint(dimension, posX, posY, posZ, 150));
		
	}
	
	public float getFriction() {
		return dataWatcher.getWatchableObjectFloat(DATAWATCHER_FRICTION);
	}
	
	public void setFriction(float friction) {
		dataWatcher.updateObject(DATAWATCHER_FRICTION, friction);
	}
	
	/**
	 * Get percent of block lift. -1 if not lifting
	 * @return
	 */
	public float getLiftPercent() {
		return dataWatcher.getWatchableObjectFloat(DATAWATCHER_LIFT);
	}
	
	public void setLiftPercent(float percent) {
		dataWatcher.updateObject(DATAWATCHER_LIFT, percent);
	}
	
	public boolean isLifting() {
		return getLiftPercent() != -1;
	}
	
	public void setNotLifting() {
		setLiftPercent(-1);
	}
	
	public float getInitialY() {
		return dataWatcher.getWatchableObjectFloat(DATAWATCHER_INITIAL_Y);
	}
	
	public void lift() {
		System.out.println("lifting start");
		setLiftPercent(0);
		if (!worldObj.isRemote) dataWatcher.updateObject(DATAWATCHER_INITIAL_Y, (float) posY);
	}
	
}
