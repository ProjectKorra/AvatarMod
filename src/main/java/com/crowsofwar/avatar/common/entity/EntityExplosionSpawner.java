package com.crowsofwar.avatar.common.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityExplosionSpawner extends AvatarEntity {

	private double maxTicksAlive;
	private float explosionStrength;
	private float frequency;

	/**
	 * @param world
	 */
	public EntityExplosionSpawner(World world) {
		super(world);
		setSize(1, 1);
		isImmuneToExplosions();
		isInvisible();

	}

	public void maxTicks(float ticks) {
		this.maxTicksAlive = ticks;
	}

	public void setExplosionStrength(float explosionStrength) {
		this.explosionStrength = explosionStrength;
	}

	public void setExplosionFrequency(float explosionFrequency) {
		this.frequency = explosionFrequency;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		setDead();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!world.isRemote && ticksExisted >= maxTicksAlive) {
			setDead();
		}

		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = world.getBlockState(below).getBlock();

		if (ticksExisted % 3 == 0) world.playSound(posX, posY, posZ,
				world.getBlockState(below).getBlock().getSoundType().getBreakSound(), SoundCategory.PLAYERS, 1, 1, false);

		float explosionSize = STATS_CONFIG.explosionSettings.explosionSize * explosionStrength;
		explosionSize += getPowerRating() * 2.0 / 100;
		if (ticksExisted >= 5 && ticksExisted % frequency == 0) {
			world.createExplosion(this, this.posX, this.posY, this.posZ, explosionSize, false);
		}

		if (!world.getBlockState(below).isNormalCube()) {
			setDead();
		}

		// Destroy if in a block
		IBlockState inBlock = world.getBlockState(getPosition());
		if (inBlock.isFullBlock()) {
			setDead();
		}

		// Destroy non-solid blocks in the spawner
		if (inBlock.getBlock() != Blocks.AIR && !inBlock.isFullBlock()) {

			if (inBlock.getBlockHardness(world, getPosition()) == 0) {

				breakBlock(getPosition());

			} else {

				setDead();
			}
		}

		for (int i = 0; i < 2; i++) {
			double x = posX + rand.nextGaussian() * 0.15;
			double z = posZ + rand.nextGaussian() * 0.15;
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, posY, z, 0, 0, 0);
		}
		if (rand.nextDouble() < 0.08) {
			double smokeX = posX + rand.nextGaussian() * 0.15;
			double smokeZ = posZ + rand.nextGaussian() * 0.15;
			world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, smokeX, posY, smokeZ, 0, 0, 0);
			for (int i = 0; i < 3; i++) {
				double fireX = posX + rand.nextGaussian() * 0.4;
				double fireY = posY + rand.nextDouble() * 0.4;
				double fireZ = posZ + rand.nextGaussian() * 0.4;
				world.spawnParticle(EnumParticleTypes.FLAME, fireX, fireY, fireZ, 0, 0, 0);
			}
		}

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
	public boolean onCollideWithSolid() {
		setDead();
		return false;
	}

}


