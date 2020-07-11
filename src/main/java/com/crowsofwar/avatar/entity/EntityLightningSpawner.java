package com.crowsofwar.avatar.entity;

import com.crowsofwar.gorecore.util.Vector;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityLightningSpawner extends AvatarEntity implements IGlowingEntity {
	private int maxTicksAlive;
	private float lightningFrequency;
	private boolean playerControl;
	private float amountofBolts;
	private float boltAccuracy;
	private double Speed;

	/**
	 * @param world The world that the entity is spawned in
	 */

	public EntityLightningSpawner(World world) {
		super(world);
		setSize(.01F, .01F);

	}

	public void setSpeed(double speed) {
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
	public void onUpdate() {
		super.onUpdate();
		if (this.getOwner() != null) {


			if (playerControl && !this.isDead && this.getOwner() != null && !world.isRemote) {
				this.rotationYaw = getOwner().rotationYaw;
				Vector direction = Vector.toRectangular(Math.toRadians(this.rotationYaw), 0);
				this.setVelocity(direction.times(Speed));
			}
			if (!world.isRemote && ticksExisted >= maxTicksAlive) {
				setDead();
			}

			float Pos = rand.nextFloat() * (boltAccuracy);
			//Does a number from 0 to 1 multiplied by the accuracy


			if (this.ticksExisted % lightningFrequency == 0 && !world.isRemote) {
				for (int i = 0; i < amountofBolts; i++) {
					int random = rand.nextInt(2) + 1;
					BlockPos blockPos = this.getPosition();
					float x = random == 1 ? Pos : -Pos;
					int y = blockPos.getY();
					float z = random == 1 ? Pos : -Pos;

					EntityAvatarLightning bolt = new EntityAvatarLightning(world, blockPos.getX() + x, y,
							blockPos.getZ() + z);
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


	@Override
	@Optional.Method(modid = "hammercore")
	public ColoredLight produceColoredLight(float partialTicks) {
		return ColoredLight.builder().pos(this).color(87, 161, 235).radius(10f).build();

	}
}

