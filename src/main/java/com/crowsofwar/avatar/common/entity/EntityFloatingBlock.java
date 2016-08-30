package com.crowsofwar.avatar.common.entity;

import static net.minecraft.network.datasync.EntityDataManager.createKey;

import java.util.List;
import java.util.Random;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.earth.EarthbendingEvent;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entityproperty.EntityPropertyDataManager;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;

import jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFloatingBlock extends Entity implements IPhysics {
	// EntitySkeleton
	
	/*
	 * 
	 * Block#getIdFromBlock, Block#getBlockById
	 * 
	 * 
	 */
	
	public static final Block DEFAULT_BLOCK = Blocks.STONE;
	
	// public static final int SYNC_BLOCKID = 2;
	// /** Whether gravity can affect the block's velocity. */
	// public static final int SYNC_GRAVITY = 3;
	// public static final int SYNC_FLOATINGBLOCKID = 4;
	//
	// public static final int SYNC_VELX = 5, SYNC_VELY = 6, SYNC_VELZ = 7,
	// SYNC_FRICTION = 8;
	// /**
	// * Whether gravity can cause the block to have negative Y velocity (gravity will still affect
	// * it).
	// */
	// public static final int SYNC_CAN_FALL = 9;
	// /** Whether the floating block breaks on contact with other blocks. */
	// public static final int SYNC_ON_LAND = 10;
	// public static final int SYNC_TARGET_BLOCK = 11; // 11,12,13,14
	// public static final int SYNC_METADATA = 15;
	
	private static final DataParameter<Boolean> SYNC_GRAVITY_ENABLED = createKey(EntityFloatingBlock.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SYNC_ENTITY_ID = createKey(EntityFloatingBlock.class,
			DataSerializers.VARINT);
	private static final DataParameter<Vector> SYNC_VELOCITY = createKey(EntityFloatingBlock.class,
			AvatarDataSerializers.SERIALIZER_VECTOR);
	private static final DataParameter<Float> SYNC_FRICTION = createKey(EntityFloatingBlock.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Boolean> SYNC_CAN_FALL = createKey(EntityFloatingBlock.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Byte> SYNC_ON_LAND = createKey(EntityFloatingBlock.class,
			DataSerializers.BYTE);
	private static final DataParameter<Optional<IBlockState>> SYNC_BLOCK = createKey(
			EntityFloatingBlock.class, DataSerializers.OPTIONAL_BLOCK_STATE);
	
	private static int nextBlockID = 0;
	
	/**
	 * Holds the current velocity. Please don't use this field directly, as it is designed to be
	 * synced. Use {@link #getVelocity()} and {@link #setVelocity(Vector)}.
	 */
	private Vector velocity;
	/**
	 * Target block to go to
	 */
	private final EntityPropertyDataManager<Optional<BlockPos>> propBlockPos;
	private final Vector internalPosition;
	
	private EntityPlayer owner;
	
	/**
	 * Whether or not to drop an ItemBlock when the floating block has been destroyed. Does not
	 * matter on client.
	 */
	private boolean enableItemDrops;
	
	public EntityFloatingBlock(World world) {
		super(world);
		setSize(0.95f, 0.95f);
		velocity = new Vector(0, 0, 0);
		setGravityEnabled(false);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			setID(nextBlockID++);
		}
		this.propBlockPos = new EntityPropertyDataManager<Optional<BlockPos>>(this, EntityFloatingBlock.class,
				DataSerializers.OPTIONAL_BLOCK_POS, Optional.absent());
		this.internalPosition = new Vector(0, 0, 0);
		
		this.enableItemDrops = true;
		
	}
	
	public EntityFloatingBlock(World world, IBlockState blockState) {
		this(world);
		setBlockState(blockState);
	}
	
	public EntityFloatingBlock(World world, IBlockState blockState, EntityPlayer owner) {
		this(world, blockState);
		setOwner(owner);
	}
	
	// Called from constructor of Entity class
	@Override
	protected void entityInit() {
		
		dataManager.register(SYNC_GRAVITY_ENABLED, false);
		dataManager.register(SYNC_ENTITY_ID, 0);
		dataManager.register(SYNC_VELOCITY, Vector.ZERO);
		dataManager.register(SYNC_FRICTION, 1f);
		dataManager.register(SYNC_CAN_FALL, false);
		dataManager.register(SYNC_ON_LAND, OnBlockLand.DO_NOTHING.getId());
		dataManager.register(SYNC_BLOCK, Optional.of(DEFAULT_BLOCK.getDefaultState()));
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setBlockState(
				Block.getBlockById(nbt.getInteger("BlockId")).getStateFromMeta(nbt.getInteger("Metadata")));
		setGravityEnabled(nbt.getBoolean("Gravity"));
		setVelocity(nbt.getDouble("VelocityX"), nbt.getDouble("VelocityY"), nbt.getDouble("VelocityZ"));
		setFriction(nbt.getFloat("Friction"));
		setCanFall(nbt.getBoolean("CanFall"));
		setOnLandBehavior(nbt.getByte("OnLand"));
		setItemDropsEnabled(nbt.getBoolean("DropItems"));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("BlockId", Block.getIdFromBlock(getBlock()));
		nbt.setInteger("Metadata", getBlock().getMetaFromState(getBlockState()));
		nbt.setBoolean("Gravity", isGravityEnabled());
		Vector velocity = getVelocity();
		nbt.setDouble("VelocityX", velocity.x());
		nbt.setDouble("VelocityY", velocity.y());
		nbt.setDouble("VelocityZ", velocity.z());
		nbt.setFloat("Friction", getFriction());
		nbt.setBoolean("CanFall", canFall());
		nbt.setByte("OnLand", getOnLandBehaviorId());
		nbt.setBoolean("DropItems", areItemDropsEnabled());
	}
	
	public Block getBlock() {
		return getBlockState().getBlock();
	}
	
	public void setBlock(Block block) {
		setBlockState(block.getDefaultState());
	}
	
	public IBlockState getBlockState() {
		Optional<IBlockState> obs = dataManager.get(SYNC_BLOCK);
		return obs.get();
	}
	
	public void setBlockState(IBlockState state) {
		dataManager.set(SYNC_BLOCK, Optional.of(state));
	}
	
	public boolean isGravityEnabled() {
		return dataManager.get(SYNC_GRAVITY_ENABLED);
	}
	
	public void setGravityEnabled(boolean gravity) {
		if (!worldObj.isRemote) dataManager.set(SYNC_GRAVITY_ENABLED, gravity);
	}
	
	/**
	 * Get the ID of this floating block. Each instance has its own unique ID. Synced between client
	 * and server.
	 */
	public int getID() {
		return dataManager.get(SYNC_ENTITY_ID);
	}
	
	public void setID(int id) {
		if (!worldObj.isRemote) dataManager.set(SYNC_ENTITY_ID, id);
	}
	
	public static EntityFloatingBlock getFromID(World world, int id) {
		for (int i = 0; i < world.loadedEntityList.size(); i++) {
			Entity e = (Entity) world.loadedEntityList.get(i);
			if (e instanceof EntityFloatingBlock && ((EntityFloatingBlock) e).getID() == id)
				return (EntityFloatingBlock) e;
		}
		return null;
	}
	
	/**
	 * Returns whether the floating block drops the block as an item when it is destroyed. Only used
	 * on server-side. By default, is true.
	 */
	public boolean areItemDropsEnabled() {
		return enableItemDrops;
	}
	
	/**
	 * Set whether the block should be dropped when it is destroyed.
	 */
	public void setItemDropsEnabled(boolean enable) {
		this.enableItemDrops = enable;
	}
	
	/**
	 * Disable dropping an item when the floating block is destroyed.
	 */
	public void disableItemDrops() {
		setItemDropsEnabled(false);
	}
	
	private void spawnCrackParticle(double x, double y, double z, double mx, double my, double mz) {
		worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, x, y, z, mx, my, mz,
				Block.getStateId(getBlockState()));
	}
	
	@Override
	public void onUpdate() {
		if (isGravityEnabled()) {
			addVelocity(new Vector(0, -9.81 / 20, 0));
			Vector vel = getVelocity();
			if (!canFall() && vel.y() < 0) {
				vel.setY(0);
				setVelocity(vel);
			}
		}
		
		if (ticksExisted == 1) {
			
			for (int i = 0; i < 10; i++) {
				double spawnX = posX + (rand.nextDouble() - 0.5);
				double spawnY = posY - 0;
				double spawnZ = posZ + (rand.nextDouble() - 0.5);
				spawnCrackParticle(spawnX, spawnY, spawnZ, 0, -0.1, 0);
			}
			
		}
		
		if (!worldObj.isRemote) setVelocity(getVelocity().times(getFriction()));
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		Vector velocity = getVelocity();
		moveEntity(velocity.x() / 20, velocity.y() / 20, velocity.z() / 20);
		motionX = velocity.x() / 20;
		motionY = velocity.y() / 20;
		motionZ = velocity.z() / 20;
		
		int x = (int) Math.floor(posX);
		int y = (int) Math.floor(posY);
		int z = (int) Math.floor(posZ);
		
	}
	
	/**
	 * Called when the block collides with another block or an entity
	 */
	private void onCollision() {
		// Spawn particles
		Random random = new Random();
		for (int i = 0; i < 7; i++) {
			spawnCrackParticle(posX, posY + 0.3, posZ, random.nextGaussian() * 0.1,
					random.nextGaussian() * 0.1, random.nextGaussian() * 0.1);
		}
		
		if (!worldObj.isRemote && areItemDropsEnabled()) {
			List<ItemStack> drops = getBlock().getDrops(worldObj, new BlockPos(this), getBlockState(), 0);
			for (ItemStack is : drops) {
				EntityItem ei = new EntityItem(worldObj, posX, posY, posZ, is);
				worldObj.spawnEntityInWorld(ei);
			}
		}
	}
	
	@Override
	public void addVelocity(Vector force) {
		setVelocity(getVelocity().plus(force));
	}
	
	@Override
	public Vector getVelocity() {
		velocity = dataManager.get(SYNC_VELOCITY);
		return velocity;
	}
	
	@Override
	public void setVelocity(Vector velocity) {
		if (!worldObj.isRemote) {
			dataManager.set(SYNC_VELOCITY, velocity);
		}
	}
	
	@Override
	public Vector getVecPosition() {
		internalPosition.setX(posX);
		internalPosition.setY(posY);
		internalPosition.setZ(posZ);
		return internalPosition;
	}
	
	public float getFriction() {
		return dataManager.get(SYNC_FRICTION);
	}
	
	public void setFriction(float friction) {
		if (!worldObj.isRemote) dataManager.set(SYNC_FRICTION, friction);
	}
	
	public boolean canFall() {
		return dataManager.get(SYNC_CAN_FALL);
	}
	
	public void setCanFall(boolean falls) {
		dataManager.set(SYNC_CAN_FALL, falls);
	}
	
	public byte getOnLandBehaviorId() {
		return dataManager.get(SYNC_ON_LAND);
	}
	
	public OnBlockLand getOnLandBehavior() {
		return OnBlockLand.getFromId(getOnLandBehaviorId());
	}
	
	public void setOnLandBehavior(byte id) {
		dataManager.set(SYNC_ON_LAND, id);
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
	
	@Nullable
	public BlockPos getMovingToBlock() {
		Optional<BlockPos> optional = propBlockPos.getValue();
		return optional.isPresent() ? optional.get() : null;
	}
	
	public void setMovingToBlock(@Nullable BlockPos pos) {
		Optional<BlockPos> optional = pos == null ? Optional.absent() : Optional.of(pos);
		propBlockPos.setValue(optional);
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
	 * Determines what the block will do when it touches a solid (non floating) block.
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
	
	public abstract class Behavior {
		
		/**
		 * Called every update tick.
		 * 
		 * @return Next behavior. Return <code>this</code> to continue the behavior.
		 */
		public abstract Behavior onUpdate();
		
		public abstract void fromBytes(PacketBuffer buf);
		
		public abstract void toBytes(PacketBuffer buf);
		
	}
	
	public class Place extends Behavior {
		
		private BlockPos placeAt;
		
		public Place(BlockPos placeAt) {
			this.placeAt = placeAt;
		}
		
		@Override
		public Behavior onUpdate() {
			BlockPos target = getMovingToBlock();
			Vector targetVec = new Vector(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
			Vector thisPos = new Vector(posX, posY, posZ);
			Vector force = targetVec.minus(thisPos);
			force.normalize();
			force.mul(3);
			setVelocity(force);
			if (!worldObj.isRemote && targetVec.sqrDist(thisPos) < 0.01) {
				
				setDead();
				worldObj.setBlockState(new BlockPos(EntityFloatingBlock.this), getBlockState());
				
				SoundType sound = getBlock().getSoundType();
				if (sound != null) {
					worldObj.playSound(null, target, sound.getBreakSound(), SoundCategory.PLAYERS,
							sound.getVolume(), sound.getPitch());
				}
				
				BendingManager.getBending(BendingManager.BENDINGID_EARTHBENDING)
						.notifyObservers(new EarthbendingEvent.BlockPlacedReached(EntityFloatingBlock.this));
				
			}
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {
			placeAt = buf.readBlockPos();
		}
		
		@Override
		public void toBytes(PacketBuffer buf) {
			buf.writeBlockPos(placeAt);
		}
		
	}
	
	public class Thrown extends Behavior {
		
		@Override
		public Behavior onUpdate() {
			if (!isDead) {
				List<Entity> collidedList = worldObj.getEntitiesWithinAABBExcludingEntity(
						EntityFloatingBlock.this, getEntityBoundingBox());
				if (!collidedList.isEmpty()) {
					Entity collided = collidedList.get(0);
					if (collided instanceof EntityLivingBase && collided != getOwner()) {
						double speed = getVelocity().magnitude();
						double multiplier = 0.25;
						collided.attackEntityFrom(AvatarDamageSource.causeFloatingBlockDamage(
								EntityFloatingBlock.this, collided), (float) (speed * multiplier));
						
						Vector motion = new Vector(collided).minus(new Vector(EntityFloatingBlock.this));
						
						motion.setY(0.08);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
						if (!worldObj.isRemote) setDead();
						onCollision();
					} else if (collided != getOwner()) {
						Vector motion = new Vector(collided).minus(new Vector(EntityFloatingBlock.this));
						motion.mul(0.3);
						motion.setY(0.08);
						collided.addVelocity(motion.x(), motion.y(), motion.z());
					}
				}
			}
			
			if (isCollided) {
				if (!worldObj.isRemote) setDead();
				onCollision();
				BendingManager.getBending(BendingManager.BENDINGID_EARTHBENDING)
						.notifyObservers(new EarthbendingEvent.BlockThrownReached(EntityFloatingBlock.this));
			}
			
			return this;
			
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
	public class PickUp extends Behavior {
		
		@Override
		public Behavior onUpdate() {
			if (ticksExisted > 20) {
				return new PlayerControlled(owner);
			}
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
	}
	
	public class PlayerControlled extends Behavior {
		
		private EntityPlayer controller;
		
		public PlayerControlled(EntityPlayer controller) {
			this.controller = controller;
		}
		
		@Override
		public Behavior onUpdate() {
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(controller,
					"Could not get player data to update PlayerControlled Floating Block");
			
			if (isGravityEnabled()) {
				setGravityEnabled(false);
			}
			
			double yaw = Math.toRadians(controller.rotationYaw);
			double pitch = Math.toRadians(controller.rotationPitch);
			Vector forward = Vector.fromYawPitch(yaw, pitch);
			Vector eye = Vector.getEyePos(controller);
			Vector target = forward.times(2).plus(eye);
			Vector motion = target.minus(new Vector(EntityFloatingBlock.this));
			motion.mul(5);
			setVelocity(motion);
			
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {
			controller = worldObj.getPlayerEntityByName(buf.readStringFromBuffer(50));
		}
		
		@Override
		public void toBytes(PacketBuffer buf) {
			buf.writeString(controller.getName());
		}
		
	}
	
}
