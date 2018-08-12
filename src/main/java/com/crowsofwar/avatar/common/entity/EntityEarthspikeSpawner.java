package com.crowsofwar.avatar.common.entity;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.crowsofwar.avatar.common.config.ConfigStats;

public class EntityEarthspikeSpawner extends AvatarEntity {

	private boolean unstoppable;
	private double maxTicksAlive;

	/**
	 * @param world
	 */
	public EntityEarthspikeSpawner(World world) {
		super(world);
		setSize(1, 1);

	}

	public void setUnstoppable(boolean isUnstoppable) {
		unstoppable = isUnstoppable;
	}

	public double getDuration() {
		return maxTicksAlive;
	}

	public void setDuration(double ticks) {
		maxTicksAlive = ticks;
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
	public void onUpdate() {
		super.onUpdate();

		if (ticksExisted >= maxTicksAlive) {
			setDead();
		}

		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = world.getBlockState(below).getBlock();

		if (ticksExisted % 3 == 0) {
			SoundType soundType = belowBlock.getSoundType(world.getBlockState(below), world, below, this);
			world.playSound(posX, posY, posZ, soundType.getBreakSound(), SoundCategory.PLAYERS, 1, 1, false);
		}

		if (!world.getBlockState(below).isNormalCube()) {
			setDead();
		}

		if (!world.isRemote && !ConfigStats.STATS_CONFIG.bendableBlocks.contains(belowBlock) && !unstoppable) {
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
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		return false;
	}

	@Override
	public boolean onCollideWithSolid() {
		setDead();
		return false;
	}

}
