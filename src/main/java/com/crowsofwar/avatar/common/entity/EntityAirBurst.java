package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

public class EntityAirBurst extends AvatarEntity {

	private static final DataParameter<Integer> SYNC_DISSIPATE = EntityDataManager
			.createKey(EntityAirBurst.class, DataSerializers.VARINT);
	public static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityAirBurst.class,
			DataSerializers.FLOAT);

	private int expandStop;
	//Sets the amount of ticks when the shockwave should stop expanding
	private double damage;

	public EntityAirBurst(World world) {
		super(world);
		this.expandStop = 30;
		this.damage = 0.5F;
		setSize(0.25f, 0.25f);

		this.putsOutFires = true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_DISSIPATE, 0);
		dataManager.register(SYNC_SIZE, 0.5f);
	}

	@Override
	public EntityLivingBase getController() {
		return getOwner();
	}

	public float getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

	public void setExpandStopTime (int time) {
		this.expandStop = time;
	}

	public void setDamage (double damage) {
		this.damage = damage;
	}


	@Override
	public void setPositionAndUpdate(double x, double y, double z) {
		if (getOwner() != null) {
			super.setPositionAndUpdate(getOwner().posX, getOwner().getEntityBoundingBox().minY, getOwner().posZ);
		}
	}

	@Override
	public void onUpdate() {
		//super.onUpdate();
		//Otherwise the entity glitches out


		EntityLivingBase owner = getOwner();
		if (owner == null) {
			return;
		}

		if (putsOutFires && ticksExisted % 2 == 0) {
			setFire(0);
			for (int x = 0; x <= 2; x++) {
				for (int z = 0; z <= 2; z++) {
					BlockPos pos = new BlockPos(posX + x * width, posY, posZ + z * width);
					if (world.getBlockState(pos).getBlock() == Blocks.FIRE) {
						world.setBlockToAir(pos);
						world.playSound(posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH,
								SoundCategory.PLAYERS, 1, 1, false);
					}
				}
			}
		}

		if (owner.isDead) {
			Expand();
			return;
		}

		setPosition(owner.posX, owner.getEntityBoundingBox().minY, owner.posZ);

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;

		float size = getSize();

		setDissipateTime(getDissipateTime() + 1);
		float mult = 1 + getDissipateTime() / 5f;
		setSize(size * mult, size * mult);
		if (getDissipateTime() >= expandStop) {
			setDead();
		}

		if (!isDead && !world.isRemote) {
			List<Entity> collidedList = world.getEntitiesWithinAABB(Entity.class,
					getEntityBoundingBox(), entity1 -> entity1 != getOwner());

			if (!collidedList.isEmpty()) {
				for (Entity entity : collidedList) {
					if (entity != getOwner() && canCollideWith(entity)) {
						onCollideWithEntity(entity);
					}

				}

			}
		}
	}


	@Override
	public void setDead() {
		super.setDead();
	}

	@Override
	public boolean isShield() {
		return true;
	}

	@Override
	protected void onCollideWithEntity(Entity entity) {
		if (canCollideWith(entity)) {

			double mult = -4 * STATS_CONFIG.airShockwaveSettings.push;
			Vector vel = position().minus(getEntityPos(entity));
			vel = vel.normalize().times(mult).plusY(0.3f);

			entity.motionX = vel.x();
			entity.motionY = vel.y();
			entity.motionZ = vel.z();

			if (entity instanceof AvatarEntity) {
				AvatarEntity avent = (AvatarEntity) entity;
				avent.setVelocity(vel);
			}
			entity.isAirBorne = true;
			AvatarUtils.afterVelocityAdded(entity);
			damageEntity(entity);
		}
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() == getOwner()) {
			return false;
		} else return entity != getOwner() && !(entity instanceof EntityXPOrb) && !(entity instanceof EntityItem);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDissipateTime(nbt.getInteger("Dissipate"));
		setSize(nbt.getFloat("Size"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Dissipate", getDissipateTime());
		nbt.setFloat("Size", getSize());
	}


	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	public int getDissipateTime() {
		return dataManager.get(SYNC_DISSIPATE);
	}

	public void setDissipateTime(int dissipate) {
		dataManager.set(SYNC_DISSIPATE, dissipate);
	}

	public void Expand() {
		if (!isDissipating()) setDissipateTime(1);
	}


	public boolean isDissipating() {
		return getDissipateTime() != 0;
	}

	private void damageEntity(Entity collided) {

		DamageSource source = AvatarDamageSource.causeAirDamage(collided, getOwner());
		boolean successfulHit = collided.attackEntityFrom(source, (float) this.damage);

		if (getOwner() != null && getAbility() instanceof AbilityAirBurst && !world.isRemote) {
			BendingData data = BendingData.get(getOwner());
			data.getAbilityData(getAbility().getName()).addXp(SKILLS_CONFIG.airShockwaveHit);
		}

		if (successfulHit) {
			BattlePerformanceScore.addMediumScore(getOwner());
		}


	}


}


