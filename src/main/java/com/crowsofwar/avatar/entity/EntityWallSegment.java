/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.entity.data.SyncedEntity;
import com.crowsofwar.avatar.entity.data.WallBehavior;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;

/**
 * @author CrowsOfWar
 */
@SuppressWarnings("Guava")
public class EntityWallSegment extends AvatarEntity implements IEntityAdditionalSpawnData {

	public static final int SEGMENT_HEIGHT = 5;

	private static final DataParameter<Optional<UUID>> SYNC_WALL = EntityDataManager.createKey(EntityWallSegment.class,
			DataSerializers.OPTIONAL_UNIQUE_ID);
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

	private final SyncedEntity<EntityWall> wallReference;
	private boolean restrictToVertical;
	private Vector initialPos;
	/**
	 * direction that all wall-segments are facing towards. Only set on server.
	 */
	private EnumFacing direction;
	private int offset;

	public EntityWallSegment(World world) {
		super(world);
		this.wallReference = new SyncedEntity<>(this, SYNC_WALL);
		this.wallReference.preventNullSaving();
		this.setSize(.9f, 5);
		this.restrictToVertical = true;
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_WALL, Optional.absent());
		for (DataParameter<Optional<IBlockState>> sync : SYNC_BLOCKS_DATA)
			dataManager.register(sync, Optional.of(Blocks.STONE.getDefaultState()));
		dataManager.register(SYNC_BEHAVIOR, new WallBehavior.Rising());
	}

	public EntityWall getWall() {
		return wallReference.getEntity();
	}

	@Override
	public boolean isShield() {
		return true;
	}

	public void setRestrictToVertical(boolean value) {
		this.restrictToVertical = value;
	}

	public Vector getInitialPos() {
		return initialPos;
	}

	public void setInitialPos(Vector pos) {
		initialPos = pos;
	}

	/**
	 * Allows this segment to reference the wall, and allows the wall to reference
	 * this segment.
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

		// Remove "drop wall" statCtrl if the wall is dropping
		if (behavior instanceof WallBehavior.Drop) {
			if (getOwner() != null) {
				BendingData.get(getOwner()).removeStatusControl(StatusControlController.DROP_WALL);
				BendingData.get(getOwner()).removeStatusControl(StatusControlController.PLACE_WALL);
				BendingData.get(getOwner()).removeStatusControl(StatusControlController.SHOOT_WALL);
				BendingData.get(getOwner()).removeStatusControl(StatusControlController.PUSH_WALL);
				BendingData.get(getOwner()).removeStatusControl(StatusControlController.PULL_WALL);
			}
		}
	}

	public EnumFacing getDirection() {
		return direction;
	}

	public void setDirection(EnumFacing dir) {
		this.direction = dir;
	}

	public int getBlocksOffset() {
		return offset;
	}

	public void setBlocksOffset(int offset) {
		this.offset = offset;
	}

	// Expose setSize method so AbilityWall can call it
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
	}

	@Override
	public void setDead() {
		super.setDead();
		if (getWall() != null)
			getWall().setDead();
	}

	/**
	 * Drops any blocks contained by this segment
	 */
	public void dropBlocks() {
		for (int i = 0; i < SEGMENT_HEIGHT; i++) {
			IBlockState state = getBlock(i);
			if (state.getBlock() != Blocks.AIR)
				world.setBlockState(new BlockPos(this).up(i + getBlocksOffset()), state);
		}
	}

	/**
	 * Places any blocks contained by this segment. Required since dropBlocks()
	 * places them one block lower than their "true" position
	 */
	public void placeBlocks() {
		for (int i = 0; i < SEGMENT_HEIGHT; i++) {
			IBlockState state = getBlock(i);
			if (state.getBlock() != Blocks.AIR)
				world.setBlockState(new BlockPos(this).up(i + getBlocksOffset() + 1), state);
		}
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void onUpdate() {

		super.onUpdate();
		ignoreFrustumCheck = true;

		if (getOwner() == null) {
			this.setDead();
		}
		// restrict to only vertical movement
		if (restrictToVertical) {
			setVelocity(velocity().withX(0).withZ(0));
		}

		WallBehavior next = (WallBehavior) getBehavior().onUpdate(this);
		if (getBehavior() != next)
			setBehavior(next);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand stack) {
		if (!this.isDead && !world.isRemote && player.capabilities.isCreativeMode && player.isSneaking()) {
			setDead();
			dropBlocks();
			markVelocityChanged();
			return true;
		}
		return false;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		wallReference.readFromNbt(nestedCompound(nbt, "Parent"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		wallReference.writeToNbt(nestedCompound(nbt, "Parent"));
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeFloat(height);
		buf.writeInt(offset);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		setSize(width, buf.readFloat());
		offset = buf.readInt();
	}

	@Override
	public void addVelocity(double x, double y, double z) {
	}

	@Override
	public boolean canPush() {
		return false;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer entityIn) {
		// Prevents some insane glitches...
		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, getCollisionBox(this));

		if (entities.size() > 0) {
			for (Entity entity : entities) {
				if (entity instanceof EntityPlayer) {
					if (getBehavior().getClass() == WallBehavior.Rising.class) {
						entity.addVelocity(entity.motionX, 0.25D, entity.motionZ);
					} else {
						Vec3d vel = entity.getPositionVector().subtract(getPositionVector()).scale(0.00025);
						entity.addVelocity(vel.x, vel.y, vel.z);
					}
				}
			}
		}
	}

	@Override
	public void onCollideWithEntity(Entity entity) {

		if (entity != getOwner()) {
			// Note... only called server-side
			double amt = 0.05;

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

			entity.motionY = 0.01;

			entity.isAirBorne = true;
			AvatarUtils.afterVelocityAdded(entity);
			if (entity instanceof AvatarEntity) {
				Vector velocity = ((AvatarEntity) entity).velocity();
				if (ns) {
					velocity = velocity.withZ(amt);
				} else {
					velocity = velocity.withX(amt);
				}
			}

			if (entity instanceof AvatarEntity) {

				AvatarEntity avEnt = (AvatarEntity) entity;
				avEnt.onCollideWithSolid();

				if (avEnt.onCollideWithSolid()) {
					entity.setDead();
					EntityLivingBase owner = getOwner();
					if (owner != null) {
						BendingData data = BendingData.get(owner);
						data.getAbilityData("wall").addXp(SKILLS_CONFIG.wallBlockedAttack);
						BattlePerformanceScore.addLargeScore(getOwner());
					}
				}

			}
			// this.setVelocity(Vector.ZERO);
			// For some reason the wall is affected by explosions and whatnot
		}
	}

	@Override
	public boolean canCollideWith(Entity entity) {

		boolean notWall = !(entity instanceof EntityWall) && !(entity instanceof EntityWallSegment);

		boolean friendlyProjectile = false;
		if (getOwner() != null) {
			AbilityData data = Bender.get(getOwner()).getData().getAbilityData("wall");
			if (data.isMaxLevel() && data.getPath() == AbilityTreePath.FIRST) {

				friendlyProjectile = entity instanceof AvatarEntity
						&& ((AvatarEntity) entity).getOwner() == this.getOwner();

			}
		}

		return notWall && !friendlyProjectile;

	}

}
