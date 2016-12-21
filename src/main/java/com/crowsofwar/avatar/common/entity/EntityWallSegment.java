package com.crowsofwar.avatar.common.entity;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.findNestedCompound;

import com.crowsofwar.avatar.common.entity.data.SyncableEntityReference;
import com.crowsofwar.avatar.common.entity.data.WallBehavior;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityWallSegment extends AvatarEntity {
	
	public static final int SEGMENT_HEIGHT = 5;
	
	private static final DataParameter<Integer> SYNC_HEIGHT = EntityDataManager
			.createKey(EntityWallSegment.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> SYNC_OFFSET = EntityDataManager
			.createKey(EntityWallSegment.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> SYNC_WALL = EntityDataManager
			.createKey(EntityWallSegment.class, DataSerializers.VARINT);
	private static final DataParameter<WallBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityWallSegment.class, WallBehavior.SERIALIZER);
	
	private static final DataParameter<Optional<IBlockState>>[] SYNC_BLOCKS_DATA;
	static {
		SYNC_BLOCKS_DATA = new DataParameter[SEGMENT_HEIGHT];
		for (int i = 0; i < SEGMENT_HEIGHT; i++) {
			SYNC_BLOCKS_DATA[i] = EntityDataManager.createKey(EntityWallSegment.class,
					DataSerializers.OPTIONAL_BLOCK_STATE);
		}
	}
	
	private final SyncableEntityReference<EntityWall> wallReference;
	/**
	 * direction that all wall-segments are facing towards. Only set on server.
	 */
	private EnumFacing direction;
	
	public EntityWallSegment(World world) {
		super(world);
		this.wallReference = new SyncableEntityReference<>(this, SYNC_WALL);
		this.setSize(0.9f, 5);
	}
	
	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_HEIGHT, SEGMENT_HEIGHT);
		dataManager.register(SYNC_OFFSET, 0);
		dataManager.register(SYNC_WALL, -1);
		for (DataParameter<Optional<IBlockState>> sync : SYNC_BLOCKS_DATA)
			dataManager.register(sync, Optional.of(Blocks.STONE.getDefaultState()));
		dataManager.register(SYNC_BEHAVIOR, new WallBehavior.Rising());
	}
	
	public EntityWall getWall() {
		return wallReference.getEntity();
	}
	
	/**
	 * Allows this segment to reference the wall, and allows the wall to
	 * reference this segment.
	 */
	public void attachToWall(EntityWall wall) {
		wallReference.setEntity(wall);
		wall.addSegment(this);
	}
	
	public IBlockState getBlock(int i) {
		IBlockState state = dataManager.get(SYNC_BLOCKS_DATA[i]).orNull();
		return state == null ? Blocks.AIR.getDefaultState() : state;
	}
	
	public void setBlock(int i, IBlockState block) {
		dataManager.set(SYNC_BLOCKS_DATA[i],
				block == null ? Optional.of(Blocks.AIR.getDefaultState()) : Optional.of(block));
	}
	
	public WallBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(WallBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
	public void setDirection(EnumFacing dir) {
		this.direction = dir;
	}
	
	public int getSyncedHeight() {
		return dataManager.get(SYNC_HEIGHT);
	}
	
	public void setSyncedHeight(int height) {
		dataManager.set(SYNC_HEIGHT, height);
	}
	
	public int getBlocksOffset() {
		return dataManager.get(SYNC_OFFSET);
	}
	
	public void setBlocksOffset(int offset) {
		dataManager.set(SYNC_OFFSET, offset);
	}
	
	@Override
	public void setDead() {
		super.setDead();
		if (getWall() != null)
			getWall().setDead();
		else
			dropBlocks();
	}
	
	/**
	 * Drops any blocks contained by this segment
	 */
	public void dropBlocks() {
		for (int i = 0; i < SEGMENT_HEIGHT; i++) {
			IBlockState state = getBlock(i);
			
			if (state.getBlock() != Blocks.AIR)
				worldObj.setBlockState(new BlockPos(this).up(i + getBlocksOffset()), state);
			
		}
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		velocity().setX(0);
		velocity().setZ(0);
		Vector vec = velocity().dividedBy(20);
		moveEntity(MoverType.SELF, vec.x(), vec.y(), vec.z());
		WallBehavior next = (WallBehavior) getBehavior().onUpdate(this);
		if (getBehavior() != next) setBehavior(next);
		System.out.println("Height: " + getSyncedHeight());
		if (height != getSyncedHeight()) setSize(.9f, getSyncedHeight());
		if (!worldObj.isRemote && height == 5) {
			for (int i = SEGMENT_HEIGHT - 1; i >= 0; i--) {
				if (getBlock(i).getBlock() == Blocks.AIR) {
					// System.out.println("Air@" + i);
					// 0->-1, 1->0, 2->1, 3->2,
					setSize(.9f, 5 - i - 1);
					int f = -i - 1;
					position().add(0, f, 0);
					setBlocksOffset(f);
					System.out.println("Air at " + i);
					System.out.println("h=" + height);
					System.out.println("f=" + f);
					setSyncedHeight(5 - i - 1);
					break;
				}
			}
		}
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand stack) {
		if (!this.isDead && !worldObj.isRemote && player.capabilities.isCreativeMode && player.isSneaking()) {
			setDead();
			setBeenAttacked();
			return true;
		}
		return false;
	}
	
	@Override
	public void applyEntityCollision(Entity entity) {
		
		// Note... only called server-side
		double amt = 0.4;
		
		boolean ns = direction == EnumFacing.NORTH || direction == EnumFacing.SOUTH;
		if (ns) {
			if (entity.posZ > this.posZ) {
				entity.posZ = this.posZ + 1.1;
			} else {
				amt = -amt;
				entity.posZ = this.posZ - 1.1;
			}
		} else {
			if (entity.posX > this.posX) {
				entity.posX = this.posX + 1.1;
			} else {
				amt = -amt;
				entity.posX = this.posX - 1.1;
			}
		}
		
		if (ns) {
			entity.motionZ = amt;
		} else {
			entity.motionX = amt;
		}
		
		entity.motionY = .25;
		
		entity.isAirBorne = true;
		if (entity instanceof EntityPlayerMP) {
			((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
		}
		if (entity instanceof AvatarEntity) {
			Vector velocity = ((AvatarEntity) entity).velocity();
			if (ns)
				velocity.setZ(amt);
			else
				velocity.setX(amt);
		}
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		wallReference.readFromNBT(findNestedCompound(nbt, "Parent"));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		wallReference.writeToNBT(findNestedCompound(nbt, "Parent"));
	}
	
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}
	
}
