package com.crowsofwar.avatar.common.entity.mob;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.*;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class EntityOffensive extends AvatarEntity {

	//Used for all entities that damage things
	private static final DataParameter<Float> SYNC_DAMAGE = EntityDataManager
			.createKey(EntityOffensive.class, DataSerializers.FLOAT);

	private AxisAlignedBB expandedHitbox;

	public EntityOffensive(World world) {
		super(world);
		this.expandedHitbox = getEntityBoundingBox();
	}

	public float getDamage() {
		return dataManager.get(SYNC_DAMAGE);
	}

	public void setDamage(float damage) {
		dataManager.set(SYNC_DAMAGE, damage);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote) {
			List<Entity> targets = world.getEntitiesWithinAABB(Entity.class, expandedHitbox);
			if (!targets.isEmpty()) {
				for (Entity hit : targets) {
					if (canDamageEntity(hit) && this != hit) {
						onCollideWithEntity(hit);
					}
				}
			}
		}
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		super.onCollideWithEntity(entity);
		if (!isPiercing())
			Explode();
		else applyPiercingCollision();

	}

	public void Explode() {
		if (world instanceof WorldServer) {
			if (getOwner() != null) {
				BendingData data = BendingData.get(getOwner());
				WorldServer World = (WorldServer) world;
				World.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, 50, 0, 0, 0, getParticleSpeed());
				world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 4.0F,
						(1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
				List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(getExplosionHitboxGrowth(),
						getExplosionHitboxGrowth(), getExplosionHitboxGrowth()),
						entity -> entity != getOwner());

				if (!collided.isEmpty()) {
					for (Entity entity : collided) {
						if (entity != getOwner() && entity != null && getOwner() != null) {
							attackEntity(entity, false);
							//Divide the result of the position difference to make entities fly
							//further the closer they are to the player.
							double dist = ( - entity.getDistance(entity)) > 1 ? ( - entity.getDistance(entity)) : 1;
							Vec3d velocity = entity.getPositionVector().subtract(this.getPositionVector());
							velocity = velocity.scale((1 / 40F)).scale(dist).add(0, getExplosionHitboxGrowth() / 50, 0);

							double x = velocity.x;
							double y = velocity.y > 0 ? velocity.z : 0.2F;
							double z = velocity.z;
							x *= getKnockbackMult().x;
							y *= getKnockbackMult().y;
							z *= getKnockbackMult().z;

							if (!entity.world.isRemote) {
								entity.motionX += x;
								entity.motionY += y;
								entity.motionZ += z;

								if (collided instanceof AvatarEntity) {
									if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment)
											&& !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
										AvatarEntity avent = (AvatarEntity) collided;
										avent.addVelocity(x, y, z);
										avent.onAirContact();
									}
									entity.isAirBorne = true;
									AvatarUtils.afterVelocityAdded(entity);
								}
							}
						}
					}
				}

			}
		}
	}

	public void applyPiercingCollision() {

	}

	public void attackEntity(Entity hit, boolean explosionDamage) {
		if (getOwner() != null && hit != null) {
			boolean ds = hit.attackEntityFrom(getDamageSource(hit), explosionDamage ? getAoeDamage() : getDamage());
			if (!ds && hit instanceof EntityDragon) {
				((EntityDragon) hit).attackEntityFromPart(((EntityDragon) hit).dragonPartBody, getDamageSource(hit),
						explosionDamage ? getAoeDamage() : getDamage());
				BattlePerformanceScore.addScore(getOwner(), getPerformanceAmount());

			}
			else if (hit instanceof EntityLivingBase && ds) {
				BattlePerformanceScore.addScore(getOwner(), getPerformanceAmount());
			}
		}
	}

	protected float getAoeDamage() {
		return 1;
	}

	protected Vec3d getKnockbackMult() {
		return new Vec3d(1, 1, 1);
	}

	protected EnumParticleTypes getParticle() {
		return AvatarParticles.getParticleFlames();
	}

	protected double getParticleSpeed() {
		return 0.02;
	}

	protected int getPerformanceAmount() {
		return 10;
	}

	protected DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeFireDamage(target, getOwner());
	}

	protected double getExpandedHitboxWidth() {
		return 0.25;
	}

	protected double getExpandedHitboxHeight() {
		return 0.25;
	}

	protected int getFireTime() {
		return 0;
	}

	protected boolean isPiercing() {
		return false;
	}

	public AxisAlignedBB getExpandedHitbox() {
		return expandedHitbox;
	}

	public double getExplosionHitboxGrowth() {
		return 1;
	}

	@Override
	public boolean canBePushed() {
		return !isPiercing();
	}

	@Override
	public void setEntityBoundingBox(@Nonnull AxisAlignedBB bb) {
		super.setEntityBoundingBox(bb);
		expandedHitbox = bb.grow(getExpandedHitboxWidth(), getExpandedHitboxHeight(), getExpandedHitboxWidth());
	}

}
