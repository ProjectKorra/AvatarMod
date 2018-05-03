package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.lightning.AbilityLightningRaze;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;


public class EntityAvatarLightning extends EntityLightningBolt {
	private static final DataParameter<Float> SYNC_DAMAGE_MULT = EntityDataManager.createKey(EntityAvatarLightning.class,
			DataSerializers.FLOAT);

	/**
	 * Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc.
	 */
	private int lightningState;
	/**
	 * A random long that is used to change the vertex of the lightning rendered in RenderLightningBolt
	 */
	public long boltVertex;
	/**
	 * Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time.
	 */
	private int boltLivingTime;

	private float damageMult;

	public float getDamageMult() {
		return dataManager.get(SYNC_DAMAGE_MULT);
	}

	public void setDamageMult(float damageMult) {
		dataManager.set(SYNC_DAMAGE_MULT, damageMult);
	}

	public void setBoltLivingTime(int livingTime) {
		this.boltLivingTime = livingTime;
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_DAMAGE_MULT, 1F);
	}

	public EntityAvatarLightning(World world, double x, double y, double z) {
		super(world, x, y, z, false);
		this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
		this.lightningState = 2;
		this.boltVertex = this.rand.nextLong();
		this.boltLivingTime = this.rand.nextInt(3) + 1;
		BlockPos blockpos = new BlockPos(this);


		if (!world.isRemote && world.getGameRules().getBoolean("doFireTick") && world.isAreaLoaded(blockpos, 10)) {
			if (world.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(world, blockpos)) {
				world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
			}

			for (int i = 0; i < 4; ++i) {
				BlockPos blockpos1 = blockpos.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);

				if (world.getBlockState(blockpos1).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(world, blockpos1)) {
					world.setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.WEATHER;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();

		if (this.lightningState == 2) {
			this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
			this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
		}

		--this.lightningState;

		if (this.lightningState < 0) {
			if (this.boltLivingTime == 0) {
				this.setDead();
			} else if (this.lightningState < -this.rand.nextInt(10)) {
				--this.boltLivingTime;
				this.lightningState = 1;

				if (!this.world.isRemote) {
					this.boltVertex = this.rand.nextLong();
					BlockPos blockpos = new BlockPos(this);

					if (this.world.getGameRules().getBoolean("doFireTick") && this.world.isAreaLoaded(blockpos, 10) && this.world.getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.FIRE.canPlaceBlockAt(this.world, blockpos)) {
						this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
					}
				}
			}
		}

		if (this.lightningState >= 0) {
			if (this.world.isRemote) {
				this.world.setLastLightningBolt(2);
			} else if (!this.world.isRemote) {
				double d0 = 3.0D;
				List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB
						(this.posX - 3.0D, this.posY - 3.0D, this.posZ - 3.0D, this.posX + 3.0D, this.posY + 6.0D + 3.0D, this.posZ + 3.0D));

				for (int i = 0; i < list.size(); ++i) {
					Entity entity = list.get(i);
					if (entity instanceof AvatarEntity) {
						entity.onStruckByLightning(this);
					} else if (entity instanceof EntityLivingBase) {
						handleCollision((EntityLivingBase) entity);
					}
				}
			}
		}
	}


	private void handleCollision(EntityLivingBase collided) {
		damageEntity(collided, 5 * damageMult);

	}

	private void damageEntity(EntityLivingBase entity, float damage) {

		if (world.isRemote) {
			return;
		}
		damage = damageMult * 5;


		EntityLightningSpawner boltSpawner = new EntityLightningSpawner(world);
		DamageSource damageSource = AvatarDamageSource.causeLightningDamage(entity, boltSpawner.getOwner());
		if (entity.attackEntityFrom(damageSource, damage)) {
			System.out.println(damageMult);

			if (boltSpawner.getOwner() != null) {
				BendingData data = BendingData.get(boltSpawner.getOwner());
				AbilityData abilityData = data.getAbilityData("lightning_raze");
				abilityData.addXp(SKILLS_CONFIG.struckWithLightning);
			}
		}

	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamageMult(nbt.getFloat("damageMult"));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("damageMult", damageMult);
	}
}