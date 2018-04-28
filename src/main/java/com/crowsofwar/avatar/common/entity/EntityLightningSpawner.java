package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityLightningSpawner extends AvatarEntity {

	private double maxTicksAlive;
	/**
	 * @param world
	 */
	public EntityLightningSpawner(World world) {
		super(world);
		setSize(1, 1);

	}

	public void setDuration(float ticks) {
		this.maxTicksAlive = ticks;
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

		Random rand = new Random();

		double boltPosX = rand.nextInt(2) + this.posX;
		double boltPosY = rand.nextInt(2) + this.posY;
		double boltPosZ = rand.nextInt(2) + this.posZ;

		if (!world.isRemote && ticksExisted >= maxTicksAlive) {
			setDead();
		}

		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = world.getBlockState(below).getBlock();

		if (ticksExisted % 3 == 0) world.playSound(posX, posY, posZ,
				world.getBlockState(below).getBlock().getSoundType().getBreakSound(),
				SoundCategory.PLAYERS, 1, 1, false);
		if (!this.isDead && !world.isRemote) {
			//add a for loop for determining the number of lightning bolts spawned
			EntityLightningBolt bolt = new EntityLightningBolt(world, boltPosX, boltPosY, boltPosZ,false);
			bolt.posX = boltPosX;
			bolt.posY = boltPosY;
			bolt.posZ = boltPosZ;

			world.spawnEntity(bolt);
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
		if (!world.isRemote) {
			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != getOwner());
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					if (attackEntity(entity)) {
						attacked++;
					}
				}
			}
		}

		if (!world.isRemote && getOwner() != null) {
			BendingData data = BendingData.get(getOwner());
			if (data != null) {
				data.getAbilityData("earthspike").addXp(SKILLS_CONFIG.earthspikeHit * attacked);
			}
		}
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		if (entity instanceof EntityLightningSpawner) {
			return false;
		}
		return entity instanceof EntityLivingBase || super.canCollideWith(entity);
	}

	@Override
	public boolean onCollideWithSolid() {
		setDead();
		return false;
	}

	private boolean attackEntity(Entity entity) {

		if (!(entity instanceof EntityItem && entity.ticksExisted <=
				10) && canCollideWith(entity)) {

			Vector push = velocity().withY(.8).times(STATS_CONFIG.ravineSettings.push);
			entity.addVelocity(push.x(), push.y(), push.z());
			AvatarUtils.afterVelocityAdded(entity);
		}
		return false;
	}
}
