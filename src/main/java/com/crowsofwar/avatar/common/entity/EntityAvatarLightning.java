package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.List;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityAvatarLightning extends AvatarEntity {
	/** Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc. */
	private int lightningState;
	/** A random long that is used to change the vertex of the lightning rendered in RenderLightningBolt */
	public long boltVertex;
	/** Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time. */
	private int boltLivingTime;
	//Can be manipulated for laser-like lightning attacks from the heavens
	private float Damage;

	public void setBoltLivingTime (int livingTime) {this.boltLivingTime = livingTime;}

	public void setDamage (float damage) {this.Damage = damage;}

    public EntityAvatarLightning(World world, double x, double y, double z)
	{
		super(world);
		this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
		this.lightningState = 2;
		this.boltLivingTime = this.rand.nextInt(3) - 1;
		this.boltVertex = this.rand.nextLong();
		BlockPos blockpos = new BlockPos(this);

		if (!world.isRemote && world.getGameRules().getBoolean("doFireTick") && (world.getDifficulty() == EnumDifficulty.NORMAL || world.getDifficulty() == EnumDifficulty.HARD) && world.isAreaLoaded(blockpos, 10))
		{
			if (world.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(world, blockpos))
			{
				world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
			}

			for (int i = 0; i < 4; ++i)
			{
				BlockPos blockpos1 = blockpos.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);

				if (world.getBlockState(blockpos1).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(world, blockpos1))
				{
					world.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	public SoundCategory getSoundCategory()
	{
		return SoundCategory.WEATHER;
	}



	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		/*if (!isDead && !world.isRemote) {
			List<Entity> collidedList = world.getEntitiesWithinAABB(Entity.class,
					getEntityBoundingBox());

			if (!collidedList.isEmpty()) {

				Entity collided = collidedList.get(0);

				if (collided instanceof AvatarEntity) {
					((AvatarEntity) collided).onFireContact();
				} else if (collided instanceof EntityLivingBase) {
					handleCollision((EntityLivingBase) collided);
				}

			}
		}**/

		if (this.lightningState == 2)
		{
			this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
			this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
		}

		--this.lightningState;

		if (this.lightningState < 0)
		{
			if (this.boltLivingTime == 0)
			{
				this.setDead();
			}
			else if (this.lightningState < -this.rand.nextInt(10))
			{
				--this.boltLivingTime;
				this.lightningState = 1;

				if (!this.world.isRemote)
				{
					this.boltVertex = this.rand.nextLong();
					BlockPos blockpos = new BlockPos(this);

					if (this.world.getGameRules().getBoolean("doFireTick") && this.world.isAreaLoaded(blockpos, 10) && this.world.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, blockpos))
					{
						this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
					}
				}
			}
		}

		if (this.lightningState >= 0)
		{
			if (this.world.isRemote)
			{
				this.world.setLastLightningBolt(2);
			}
		}
	}

	protected void entityInit()
	{
	}
	/*private void handleCollision(EntityLivingBase collided) {

		DamageSource source = AvatarDamageSource.causeLightningDamage(collided, getOwner());

		boolean successfulHit = collided.attackEntityFrom(source, Damage);

		Vector motion = velocity();
		motion = motion.times(STATS_CONFIG.airbladeSettings.push).withY(0.08);
		collided.addVelocity(motion.x(), motion.y(), motion.z());

	/*	if (getOwner() != null) {
			BendingData data = getOwner().getDataManager();
			data.getAbilityData("airblade").addXp(SKILLS_CONFIG.airbladeHit);
		}**/

		/*if (successfulHit) {
			BattlePerformanceScore.addSmallScore(getOwner());
		}
	}**/
	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
	}
}
