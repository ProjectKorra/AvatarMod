package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.lightning.AbilityLightningRaze;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

@SuppressWarnings("EntityConstructor")
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityAvatarLightning extends EntityLightningBolt implements IGlowingEntity {
	//TODO: Fix by using the new rendering system, make the spawner use behaviour

	/**
	 * A random long that is used to change the vertex of the lightning rendered in RenderLightningBolt
	 **/
	public long boltVertex;
	/**
	 * Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc.
	 */
	private int lightningState;
	/**
	 * Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time.
	 */
	private int boltLivingTime;

	private EntityLightningSpawner spawner;

	EntityAvatarLightning(World world, double x, double y, double z) {
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

	void setSpawner(EntityLightningSpawner spawner) {
		this.spawner = spawner;
	}

	void setBoltLivingTime(int livingTime) {
		this.boltLivingTime = livingTime;
	}

	@Override
	public void entityInit() {
		super.entityInit();
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.WEATHER;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {


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
				List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB
						(this.posX - 1.50D, this.posY - 1.5D, this.posZ - 1.5D, this.posX + 1.5D, this.posY + 6.0D + 1.5D, this.posZ + 1.5D));

				for (Entity entity : list) {
					if (entity instanceof AvatarEntity) {
						((AvatarEntity) entity).onFireContact();
					} else if ((!(entity instanceof EntityItem) && !(entity instanceof EntityItemFrame) &&
							!(entity instanceof EntityPainting) && !(entity instanceof EntityXPOrb))) {
						handleCollision(entity);
					}

				}
			}
		}

	}

	private void handleCollision(Entity collided) {
		damageEntity(collided);
		collided.setFire(8);
	}


	private void damageEntity(Entity entity) {
		if (world.isRemote) return;

		DamageSource damageSource = AvatarDamageSource.causeLightningDamage(entity, spawner.getOwner());
		float damage = STATS_CONFIG.lightningRazeSettings.damage;

		if (spawner.getAbility() instanceof AbilityLightningRaze) {
			AbilityData aD = AbilityData.get(spawner.getOwner(), spawner.getAbility().getName());
			if (aD.getLevel() >= 2) {
				damage = STATS_CONFIG.lightningRazeSettings.damage * 1.5F;
				//3
			}
			if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				damage = STATS_CONFIG.lightningRazeSettings.damage * 2.5F;
				//5
			}
			if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				damage = STATS_CONFIG.lightningRazeSettings.damage * 0.25F;
				//2
			}
		}
		if (entity.attackEntityFrom(damageSource, damage)) {
			if (spawner.getOwner() != null) {
				BendingData data1 = BendingData.get(spawner.getOwner());
				AbilityData abilityData1 = data1.getAbilityData("lightning_raze");
				abilityData1.addXp(SKILLS_CONFIG.struckWithLightning);
			}
		}

	}


	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	@Optional.Method(modid = "hammercore")
	public ColoredLight produceColoredLight(float v) {
		return ColoredLight.builder().color(1F, 2F, 3F).radius(15).pos(this).build();
	}
}