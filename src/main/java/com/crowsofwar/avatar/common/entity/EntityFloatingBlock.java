package com.crowsofwar.avatar.common.entity;

import java.util.List;
import java.util.Random;

import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.VectorUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFloatingBlock extends Entity {
	
	public static final Block DEFAULT_BLOCK = Blocks.stone;
	public static final int DATAWATCHER_BLOCKID = 2;
	/** Whether gravity can affect the block's velocity. */
	public static final int DATAWATCHER_GRAVITY = 3;
	public static final int DATAWATCHER_FLOATINGBLOCKID = 4;
	
	public static final int DATAWATCHER_VELX = 5,
			DATAWATCHER_VELY = 6, DATAWATCHER_VELZ = 7, DATAWATCHER_FRICTION = 8;
	/** Whether gravity can cause the block to have negative Y velocity (gravity will still affect it). */
	public static final int DATAWATCHER_CAN_FALL = 9;
	/** Whether the floating block breaks on contact with other blocks. */
	public static final int DATAWATCHER_IS_DESTROYABLE = 10;
	
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
			System.out.println(getFromID(worldObj, getID()));
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
		
		dataWatcher.addObject(DATAWATCHER_FRICTION, 1f);
		dataWatcher.addObject(DATAWATCHER_CAN_FALL, (byte) 0);
		dataWatcher.addObject(DATAWATCHER_IS_DESTROYABLE, (byte) 0);
		
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
		if (!worldObj.isRemote)
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
		if (!worldObj.isRemote)
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
			addForce(Vec3.createVectorHelper(0, -9.81 / 20, 0));
			Vec3 vel = getVelocity();
			if (!canFall() && vel.yCoord < 0) {
				vel.yCoord = 0;
				setVelocity(vel);
			}
		}
		
		if (!worldObj.isRemote) setVelocity(VectorUtils.times(getVelocity(), getFriction()));
		
		moveEntity(getVelocity().xCoord / 20, getVelocity().yCoord / 20, getVelocity().zCoord / 20);
		if (canBeDestroyed() && isCollided) {
			setDead();
			
			// Spawn particles
			Random random = new Random();
			for (int i = 0; i < 7; i++) {
				worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(getBlock()) + "_" + getMetadata(),
						posX, posY + 0.3, posZ, random.nextGaussian() * 0.1, random.nextGaussian() * 0.1, random.nextGaussian() * 0.1);
			}
			
			if (!worldObj.isRemote) {
				List<ItemStack> drops = getBlock().getDrops(worldObj, 0, 0, 0, getMetadata(), 0);
				for (ItemStack is : drops) {
					EntityItem ei = new EntityItem(worldObj, posX, posY, posZ, is);
					worldObj.spawnEntityInWorld(ei);
				}
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
	
	public float getFriction() {
		return dataWatcher.getWatchableObjectFloat(DATAWATCHER_FRICTION);
	}
	
	public void setFriction(float friction) {
		if (!worldObj.isRemote)
			dataWatcher.updateObject(DATAWATCHER_FRICTION, friction);
	}
	
	public boolean canFall() {
		return dataWatcher.getWatchableObjectByte(DATAWATCHER_CAN_FALL) == 1;
	}
	
	public void setCanFall(boolean falls) {
		dataWatcher.updateObject(DATAWATCHER_CAN_FALL, (byte) (falls ? 1 : 0));
	}
	
	public boolean canBeDestroyed() {
		return dataWatcher.getWatchableObjectByte(DATAWATCHER_IS_DESTROYABLE) == 1;
	}
	
	public void setDestroyable(boolean destroyable) {
		if (!worldObj.isRemote)
			dataWatcher.updateObject(DATAWATCHER_IS_DESTROYABLE, (byte) (destroyable ? 1 : 0));
	}
	
	/**
	 * Drop the block - enable gravity, can fall, and can be destroyed.
	 */
	public void drop() {
		setGravityEnabled(true);
		setCanFall(true);
		setDestroyable(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double d) {
		return true;
	}
	
}
