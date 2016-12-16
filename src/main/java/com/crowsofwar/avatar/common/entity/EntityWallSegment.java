package com.crowsofwar.avatar.common.entity;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.findNestedCompound;

import com.crowsofwar.avatar.common.entity.data.SyncableEntityReference;
import com.crowsofwar.avatar.common.entity.data.WallBehavior;
import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
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
	
	public EntityWallSegment(World world) {
		super(world);
		this.wallReference = new SyncableEntityReference<>(this, SYNC_WALL);
		this.setSize(1, 5);
	}
	
	@Override
	public void entityInit() {
		super.entityInit();
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
		return dataManager.get(SYNC_BLOCKS_DATA[i]).orNull();
	}
	
	public void setBlock(int i, IBlockState block) {
		dataManager.set(SYNC_BLOCKS_DATA[i], block == null ? Optional.absent() : Optional.of(block));
	}
	
	public WallBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(WallBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
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
		if (!worldObj.isRemote) {
			for (int i = 0; i < SEGMENT_HEIGHT; i++) {
				System.out.println("Dropping " + getBlock(i));
				IBlockState state = getBlock(i);
				Block block = state.getBlock();
				// entityDropItem(new ItemStack(state.getBlock(), 1,
				// block.getMetaFromState(state)), i);
				
				// put back
				worldObj.setBlockState(new BlockPos(this).down(5 - i), state);
				
			}
		}
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		this.noClip = false;
		WallBehavior next = (WallBehavior) getBehavior().onUpdate(this);
		if (getBehavior() != next) setBehavior(next);
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand stack) {
		System.out.println("Attacked");// EntityItemFrame Minecraft
		if (!this.isDead && !worldObj.isRemote) {
			setDead();
			setBeenAttacked();
			return true;
		}
		return false;
	}
	
	@Override
	public void applyEntityCollision(Entity entity) {
		
		double amt = 0.4;
		
		// entity.motionZ = velocity;
		if (entity.posZ > this.posZ) {
			entity.posZ = this.posZ + 1.1;
		} else {
			amt = -amt;
			entity.posZ = this.posZ - 1.1;
		}
		entity.motionZ = amt;
		entity.motionY = .25;
		
		entity.isAirBorne = true;
		if (entity instanceof EntityPlayerMP) {
			((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
		}
		if (entity instanceof AvatarEntity) {
			((AvatarEntity) entity).velocity().setZ(amt);
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
	
}
