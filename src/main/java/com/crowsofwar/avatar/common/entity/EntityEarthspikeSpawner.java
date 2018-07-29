package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.earth.AbilityEarthspikes;
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

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
		this.unstoppable = isUnstoppable;
	}

	public void setDuration(double ticks) {
		this.maxTicksAlive = ticks;
	}

	public double getDuration() {
		return this.maxTicksAlive;
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

		if (ticksExisted % 3 == 0) world.playSound(posX, posY, posZ,
				world.getBlockState(below).getBlock().getSoundType().getBreakSound(),
				SoundCategory.PLAYERS, 1, 1, false);

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
