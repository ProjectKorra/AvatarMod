package com.crowsofwar.avatar.common.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.crowsofwar.gorecore.util.Vector;

public class EntityLightningSpawner extends AvatarEntity {
	private int maxTicksAlive;
	private float lightningFrequency;
	private boolean playerControl;
	private float amountOfBolts;
	private float boltAccuracy;
	private double Speed;

	public EntityLightningSpawner(World world) {
		super(world);
		setSize(.01F, .01F);

	}

	public void setSpeed(double speed) {
		Speed = speed;
	}

	public void setDuration(int ticks) {
		maxTicksAlive = ticks;
	}

	public void setLightningFrequency(float ticks) {
		lightningFrequency = ticks;
	}

	public void setPlayerControl(boolean shouldControl) {
		playerControl = shouldControl;
	}

	public void setAmountOfBolts(float amount) {
		amountOfBolts = amount;
	}

	public void setAccuracy(float accuracy) {
		boltAccuracy = accuracy;
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
		if (getOwner() != null) {

			if (playerControl && !isDead && getOwner() != null && !world.isRemote) {
				rotationYaw = getOwner().rotationYaw;
				Vector direction = Vector.toRectangular(Math.toRadians(rotationYaw), 0);
				setVelocity(direction.times(Speed));
			}
			if (!world.isRemote && ticksExisted >= maxTicksAlive) {
				setDead();
			}

			float Pos = rand.nextFloat() * (boltAccuracy);
			//Does a number from 0 to 1 multiplied by the accuracy

			if (ticksExisted % lightningFrequency == 0 && !world.isRemote) {
				for (int i = 0; i < amountOfBolts; i++) {
					int random = rand.nextInt(2) + 1;
					BlockPos blockPos = getPosition();
					float x = random == 1 ? Pos : -Pos;
					int y = blockPos.getY();
					float z = random == 1 ? Pos : -Pos;

					EntityAvatarLightning bolt = new EntityAvatarLightning(world, blockPos.getX() + x, y, blockPos.getZ() + z);
					bolt.setBoltLivingTime(rand.nextInt(3) + 1);
					//Damage is calculated in the lightning bolt's class.
					bolt.setSpawner(this);
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
	public boolean canCollideWith(Entity entity) {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean onCollideWithSolid() {
		return false;
	}

}

