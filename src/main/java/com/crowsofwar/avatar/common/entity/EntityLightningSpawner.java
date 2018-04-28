package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.BendingData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import java.util.Random;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

public class EntityLightningSpawner extends AvatarEntity {
	private float maxTicksAlive;
	private float lightningFrequency;
	private boolean trackEnemies;
	private float amountofBolts;
	private float accuray;

	/**
	 * @param world
	 */
	public EntityLightningSpawner(World world) {
		super(world);
		setSize(.01F, .01F);

	}

	public void setDuration(float ticks) {
		this.maxTicksAlive = ticks;
	}

	public void setLightningFrequency(float ticks) {this.lightningFrequency = ticks;}

	public void setTrackEnemies(boolean shouldTrack) {this.trackEnemies = shouldTrack;}

	public void setAmountofBolts (float amount) {this.amountofBolts = amount;}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDead();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		Random random = new Random();
		double Pos = random.nextInt(2);

		if (!world.isRemote && ticksExisted >= maxTicksAlive) {
			setDead();
		}

		BlockPos below = getPosition().offset(EnumFacing.DOWN);

		if (this.ticksExisted % lightningFrequency == 0 && !world.isRemote) {
			BlockPos blockPos = this.getPosition();
			EntityLightningBolt bolt = new EntityLightningBolt(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), false);
			world.addWeatherEffect(bolt);
		}

		if (!world.getBlockState(below).isNormalCube()) {
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

		// amount of entities which were successfully attacked
		int attacked = 0;

		// Push collided entities back



		if (!world.isRemote && getOwner() != null) {
			BendingData data = BendingData.get(getOwner());
			if (data != null) {
				data.getAbilityData("lightning_raze").addXp(SKILLS_CONFIG.lightningspearHit);
			}
		}
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		if (entity instanceof EntityLightningSpawner || entity instanceof EntityLivingBase) {
			return false;
		}
		return entity instanceof EntityShield || super.canCollideWith(entity);
	}

	@Override
	public boolean onCollideWithSolid() {
		return false;
	}


}

