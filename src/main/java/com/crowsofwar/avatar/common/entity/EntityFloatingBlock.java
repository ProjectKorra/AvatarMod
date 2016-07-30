package com.crowsofwar.avatar.common.entity;

import java.util.List;
import java.util.Random;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyBlockPos;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.BlockPos;
import com.crowsofwar.avatar.common.util.VectorUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFloatingBlock extends Entity implements IPhysics {
	
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
	public static final int DATAWATCHER_ON_LAND = 10;
	public static final int DATAWATCHER_TARGET_BLOCK = 11;
	
	private static int nextBlockID = 0;
	
	/**
	 * Holds the current velocity. Please don't use this field directly,
	 * as it is not designed to be synced. Use {@link #getVelocity()}
	 * and {@link #setVelocity(Vec3)}.
	 */
	private final Vec3 velocity;
	private final EntityPropertyBlockPos propBlockPos;
	private final Vec3 internalPosition;
	
	private EntityPlayer owner;
	
	public EntityFloatingBlock(World world) {
		super(world);
		setSize(0.95f, 0.95f);
		velocity = Vec3.createVectorHelper(0, 0, 0);
		setGravityEnabled(false);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			System.out.println("Constructed with ID " + nextBlockID);setID(nextBlockID++);
			System.out.println(getFromID(worldObj, getID()));
		}
		this.propBlockPos = new EntityPropertyBlockPos(this, dataWatcher, DATAWATCHER_TARGET_BLOCK);
		this.internalPosition = Vec3.createVectorHelper(0, 0, 0);
	}
	
	public EntityFloatingBlock(World world, Block block) {
		this(world);
		setBlock(block);
	}
	
	public EntityFloatingBlock(World world, Block block, EntityPlayer owner) {
		this(world, block);
		setOwner(owner);
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
		dataWatcher.addObject(DATAWATCHER_ON_LAND, (byte) 0);
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setBlock(Block.getBlockFromName(nbt.getString("Block")));
		setGravityEnabled(nbt.getBoolean("Gravity"));
		setVelocity(nbt.getDouble("VelocityX"), nbt.getDouble("VelocityY"), nbt.getDouble("VelocityZ"));
		setFriction(nbt.getFloat("Friction"));
		setCanFall(nbt.getBoolean("CanFall"));
		setOnLandBehavior(nbt.getByte("OnLand"));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("Block", getNameForBlock(getBlock()));
		nbt.setBoolean("Gravity", isGravityEnabled());
		Vec3 velocity = getVelocity();
		nbt.setDouble("VelocityX", velocity.xCoord);
		nbt.setDouble("VelocityY", velocity.yCoord);
		nbt.setDouble("VelocityZ", velocity.zCoord);
		nbt.setFloat("Friction", getFriction());
		nbt.setBoolean("CanFall", canFall());
		nbt.setByte("OnLand", getOnLandBehaviorId());
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
		if (isGravityEnabled()) {
			addVelocity(Vec3.createVectorHelper(0, -9.81 / 20, 0));
			Vec3 vel = getVelocity();
			if (!canFall() && vel.yCoord < 0) {
				vel.yCoord = 0;
				setVelocity(vel);
			}
		}
		
		if (!worldObj.isRemote) setVelocity(VectorUtils.times(getVelocity(), getFriction()));
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		Vec3 velocity = getVelocity();
		moveEntity(velocity.xCoord / 20, velocity.yCoord / 20, velocity.zCoord / 20);
		motionX = velocity.xCoord / 20;
		motionY = velocity.yCoord / 20;
		motionZ = velocity.zCoord / 20;
		if (isCollided) {
			switch (getOnLandBehavior()) {
				case BREAK:
					if (!worldObj.isRemote) setDead();
					onCollision();
					break;
				case PLACE:
					if (!worldObj.isRemote) {
						// TODO Fix duplicate placing code (Weird!) - I don't believe this part ever gets called.
						setDead();
						int x = (int) Math.floor(posX);
						int y = (int) Math.floor(posY);
						int z = (int) Math.floor(posZ);
						worldObj.setBlock(x, y, z, getBlock());
						worldObj.setBlockMetadataWithNotify(x, y, z, getMetadata(), 3);
					}
					break;
				default: break;
			}
		}
		
		int x = (int) Math.floor(posX);
		int y = (int) Math.floor(posY);
		int z = (int) Math.floor(posZ);
		
		if (isMovingToBlock()) {
			BlockPos target = getMovingToBlock();
			Vec3 targetVec = Vec3.createVectorHelper(target.x + 0.5, target.y + 0.5, target.z + 0.5);
			Vec3 thisPos = Vec3.createVectorHelper(posX, posY, posZ);
			Vec3 force = VectorUtils.minus(targetVec, thisPos);
			force.normalize();
			VectorUtils.mult(force, 3);
			setVelocity(force);
			if (!worldObj.isRemote && targetVec.squareDistanceTo(thisPos) < 0.0003) {
				
				setDead();
				worldObj.setBlock(x, y, z, getBlock());
				worldObj.setBlockMetadataWithNotify(x, y, z, getMetadata(), 3);
				Block.SoundType sound = getBlock().stepSound;
				if (sound != null) worldObj.playSoundAtEntity(this, sound.getBreakSound(), sound.getVolume(), sound.getPitch());
				
			}
		}
		
		
		if (!isDead) {
			List<Entity> collidedList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox);
			if (!collidedList.isEmpty()) {
				Entity collided = collidedList.get(0);
				if (collided instanceof EntityLivingBase && collided != getOwner()) {
					double speed = getVelocity().lengthVector();
					double multiplier = 0.25;
					collided.attackEntityFrom(DamageSource.anvil, (float) (speed * multiplier)); // TODO custom damagesource
					Vec3 motion = VectorUtils.minus(VectorUtils.getEntityPos(collided), VectorUtils.getEntityPos(this));
					motion.yCoord = 0.08;
					collided.addVelocity(motion.xCoord, motion.yCoord, motion.zCoord);
					if (!worldObj.isRemote) setDead();
					onCollision();
				} else if (collided != getOwner()) {
					Vec3 motion = VectorUtils.minus(VectorUtils.getEntityPos(collided), VectorUtils.getEntityPos(this));
					VectorUtils.mult(motion, 0.3);
					motion.yCoord = 0.08;
					collided.addVelocity(motion.xCoord, motion.yCoord, motion.zCoord);
				}
			}
		}
//		setDead();
		if (ticksExisted % 5 == 0) propBlockPos.sync();
		
	}
	
	/**
	 * Called when the block collides with another block or an entity
	 */
	private void onCollision() {
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
	
	@Override
	public void addVelocity(Vec3 force) {
		setVelocity(VectorUtils.plus(getVelocity(), force));
	}
	
	@Override
	public Vec3 getVelocity() {
		velocity.xCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELX);
		velocity.yCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELY);
		velocity.zCoord = dataWatcher.getWatchableObjectFloat(DATAWATCHER_VELZ);
		return velocity;
	}
	
	@Override
	public void setVelocity(Vec3 velocity) {
		if (!worldObj.isRemote) {
			dataWatcher.updateObject(DATAWATCHER_VELX, (float) velocity.xCoord);
			dataWatcher.updateObject(DATAWATCHER_VELY, (float) velocity.yCoord);
			dataWatcher.updateObject(DATAWATCHER_VELZ, (float) velocity.zCoord);
		}
	}
	
	@Override
	public Vec3 getPosition() {
		internalPosition.xCoord = posX;
		internalPosition.yCoord = posY;
		internalPosition.zCoord = posZ;
		return internalPosition;
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
	
	public byte getOnLandBehaviorId() {
		return dataWatcher.getWatchableObjectByte(DATAWATCHER_ON_LAND);
	}
	
	public OnBlockLand getOnLandBehavior() {
		return OnBlockLand.getFromId(getOnLandBehaviorId());
	}
	
	public void setOnLandBehavior(byte id) {
		dataWatcher.updateObject(DATAWATCHER_ON_LAND, id);
	}
	
	public void setOnLandBehavior(OnBlockLand onLand) {
		setOnLandBehavior(onLand.getId());
	}
	
	public boolean canBeDestroyed() {
		return getOnLandBehavior() == OnBlockLand.BREAK;
	}
	
	/**
	 * Drop the block - enable gravity, can fall, and can be destroyed.
	 */
	public void drop() {
		setGravityEnabled(true);
		setCanFall(true);
		setOnLandBehavior(OnBlockLand.BREAK);
	}
	
	public EntityPlayer getOwner() {
		return owner;
	}
	
	public void setOwner(EntityPlayer owner) {
		this.owner = owner;
	}
	
	public BlockPos getMovingToBlock() {
		return propBlockPos.getValue();
	}
	
	public void setMovingToBlock(BlockPos pos) {
		propBlockPos.setValue(pos);
	}
	
	public boolean isMovingToBlock() {
		return getMovingToBlock() != null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double d) {
		return true;
	}
	
	/**
	 * Determines what the block will do when
	 * it touches a solid (non floating) block.
	 *
	 */
	public static enum OnBlockLand {
		
		/** Do nothing */
		DO_NOTHING,
		/** Break the floating block and drop its item */
		BREAK,
		/** Place the floating block into the world */
		PLACE;
		
		public byte getId() {
			return (byte) ordinal();
		}
		
		public static OnBlockLand getFromId(byte id) {
			return values()[id];
		}
		
	}
	
}
