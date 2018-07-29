package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.lightning.AbilityLightningRaze;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;


public class EntityLightningSpawner extends AvatarEntity {
	private int maxTicksAlive;
	private float lightningFrequency;
	private boolean playerControl;
	private float amountofBolts;
	private float boltAccuracy;
	private int Speed;
	private float damageMult;
	Random random = new Random();
	/**
	 * @param world
	 */

	public EntityLightningSpawner(World world) {
		super(world);
		setSize(.01F, .01F);

	}

	/*public void setDamageMult(float damageMult) {
		dataManager.set(SYNC_DAMAGE_MULT, damageMult);
	}**/

	public void setDamageMult(float mult) {
		this.damageMult = mult;
	}

	public void setSpeed(int speed) {
		this.Speed = speed;
	}

	public void setDuration(int ticks) {
		this.maxTicksAlive = ticks;
	}

	public void setLightningFrequency(float ticks) {
		this.lightningFrequency = ticks;
	}

	public void setPlayerControl(boolean shouldControl) {
		this.playerControl = shouldControl;
	}

	public void setAmountofBolts(float amount) {
		this.amountofBolts = amount;
	}

	public void setAccuracy(float accuracy) {
		this.boltAccuracy = accuracy;
	}


	@Override
	protected void entityInit() {
		super.entityInit();
	}

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
		if (this.getOwner() != null) {


			if (playerControl && !this.isDead && this.getOwner() != null) {
				this.rotationYaw = getOwner().rotationYaw;
				Vector direction = Vector.toRectangular(Math.toRadians(this.rotationYaw), 0);
				this.setVelocity(direction.times(Speed));
			}
			if (!world.isRemote && ticksExisted >= maxTicksAlive) {
				setDead();
			}

			float Pos = 0 + rand.nextFloat() * (boltAccuracy - 0);

			if (this.ticksExisted % lightningFrequency == 0 && !world.isRemote) {
				for (int i = 0; i < amountofBolts; i++) {
						BlockPos blockPos = this.getPosition();
						EntityAvatarLightning bolt = new EntityAvatarLightning(world, blockPos.getX() + Pos, blockPos.getY(),
								blockPos.getZ() + Pos);
						bolt.setBoltLivingTime(random.nextInt(3) + 1);
						bolt.setMult(damageMult);
						world.addWeatherEffect(bolt);

					}
				}
			}
			BlockPos below = getPosition().offset(EnumFacing.DOWN);

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

