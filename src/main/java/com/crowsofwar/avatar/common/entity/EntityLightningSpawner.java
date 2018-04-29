package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

public class EntityLightningSpawner extends AvatarEntity {
	private float maxTicksAlive;
	private float lightningFrequency;
	private boolean playerControl;
	private float amountofBolts;
	private float boltAccuracy;
	private int Speed;

	/**
	 * @param world
	 */
	public EntityLightningSpawner(World world) {
		super(world);
		setSize(.01F, .01F);

	}

	public void setSpeed(int speed) {this.Speed = speed;}

	public void setDuration(float ticks) {
		this.maxTicksAlive = ticks;
	}

	public void setLightningFrequency(float ticks) {this.lightningFrequency = ticks;}

	public void setPlayerControl(boolean shouldControl) {this.playerControl = playerControl;}

	public void setAmountofBolts (float amount) {this.amountofBolts = amount;}

	public void setAccuracy (float accuracy) {this.boltAccuracy = accuracy;}





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

		if (playerControl) {
			this.rotationYaw = getOwner().rotationYaw;
			Vector direction = Vector.toRectangular(Math.toRadians(this.rotationYaw), 0);
			this.setVelocity(direction.times(Speed));
		}
		float Pos = 0 + rand.nextFloat() * (boltAccuracy - 0);

		if (!world.isRemote && ticksExisted >= maxTicksAlive) {
			setDead();
		}

		BlockPos below = getPosition().offset(EnumFacing.DOWN);

		if (this.ticksExisted % lightningFrequency == 0 && !world.isRemote) {
			if (amountofBolts == 1) {
				BlockPos blockPos = this.getPosition();
				EntityLightningBolt bolt = new EntityLightningBolt(world, blockPos.getX() + Pos, blockPos.getY(),
						blockPos.getZ() + Pos, false);
				world.addWeatherEffect(bolt);
			}
			else {
				for (int i = 0; i<amountofBolts; i++){
					BlockPos blockPos = this.getPosition();
					EntityLightningBolt bolt = new EntityLightningBolt(world, blockPos.getX() + Pos, blockPos.getY(),
							blockPos.getZ() + Pos, false);
					world.addWeatherEffect(bolt);

				}
			}
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

