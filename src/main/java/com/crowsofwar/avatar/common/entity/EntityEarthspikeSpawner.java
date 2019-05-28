package com.crowsofwar.avatar.common.entity;

import java.util.UUID;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.entity.data.EarthspikesBehavior;
import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityEarthspikeSpawner extends AvatarEntity {

	private boolean unstoppable;
	private double maxTicksAlive;
	private SpikesType type;
	private double spikeSize;
	private double spikeDamage;
	private AbilityTreePath path;

	/**
	 * @param world
	 */
	public EntityEarthspikeSpawner(World world) {
		super(world);
		setSize(1, 1);
	}

	public void setType(SpikesType isType) {
		this.type = isType;
	}

	public void setPath(AbilityTreePath path) {
		this.path = path;
	}

	public AbilityTreePath getPath() {
		return path;
	}

	public SpikesType getType() {
		return type;
	}

	public void setUnstoppable(boolean isUnstoppable) {
		this.unstoppable = isUnstoppable;
	}

	public boolean getUnstoppable() {
		return unstoppable;
	}

	public double getDuration() {
		return this.maxTicksAlive;
	}

	public void setDuration(double ticks) {
		this.maxTicksAlive = ticks;
	}

	public double getSize() {
		return this.spikeSize;
	}

	public void setSize(double size) {
		this.spikeSize = size;
	}

	public double getDamage() {
		return this.spikeDamage;
	}

	public void setDamage(double damages) {
		this.spikeDamage = damages;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	public EntityLivingBase getController() {
		return getOwner();
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean onCollideWithSolid() {
		setDead();
		return false;
	}

	private static final DataParameter<EarthspikesBehavior> SPIKES_BEHAVIOR = EntityDataManager
			.createKey(EntityEarthspikeSpawner.class, EarthspikesBehavior.SERIALIZER);

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SPIKES_BEHAVIOR, new EarthspikesBehavior.Spawn());
	}

	public EarthspikesBehavior getBehavior() {
		return dataManager.get(SPIKES_BEHAVIOR);
	}

	public void setBehavior(EarthspikesBehavior behavior) {
		dataManager.set(SPIKES_BEHAVIOR, behavior);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (ticksExisted >= maxTicksAlive) {
			setDead();
		}

		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = world.getBlockState(below).getBlock();

		if (ticksExisted % 3 == 0)
			world.playSound(posX, posY, posZ, world.getBlockState(below).getBlock().getSoundType().getBreakSound(),
					SoundCategory.PLAYERS, 1, 1, false);

		if (!world.getBlockState(below).isNormalCube()) {
			setDead();
		}

		if (!world.isRemote && !ConfigStats.STATS_CONFIG.bendableBlocks.contains(belowBlock) && !unstoppable) {
			setDead();
		}

		if (!world.isRemote && belowBlock == Blocks.AIR) {
			setDead();
		}

		// Destroy if in a block
		IBlockState inBlock = world.getBlockState(getPosition());
		if (inBlock.isFullBlock()) {
			setDead();
		}

		// Destroy non-solid blocks in the earthspike
		if (inBlock.getBlock() != Blocks.AIR && !inBlock.isFullBlock()) {
			if (inBlock.getBlockHardness(world, getPosition()) == 0) {
				breakBlock(getPosition());
			} else {
				setDead();
			}
		}

		EarthspikesBehavior next = (EarthspikesBehavior) getBehavior().onUpdate(this);
		if (getBehavior() != next)
			setBehavior(next);
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public BendingStyle getElement() {
		return new Earthbending();
	}

	// Allows setting the spikes type
	public enum SpikesType {
		LINE, OCTOPUS
	};
}
