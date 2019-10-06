package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public interface IOffensiveEntity {
	/*public static void Explode(World world, AvatarEntity entity, EntityLivingBase owner) {
		if (world instanceof WorldServer) {
			if (owner != null) {
				WorldServer World = (WorldServer) world;
				World.spawnParticle(getParticle(), posX, posY, posZ, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
				world.playSound(null, posX, posY, posZ, getSound(), getSoundCategory(), getVolume(),
						getPitch());
				List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox().grow(getExplosionHitboxGrowth(),
						getExplosionHitboxGrowth(), getExplosionHitboxGrowth()),
						entity -> entity != getOwner());

				if (!collided.isEmpty()) {
					for (Entity entity : collided) {
						if (entity != getOwner() && entity != null && getOwner() != null) {
							attackEntity(entity, false);
							//Divide the result of the position difference to make entities fly
							//further the closer they are to the player.
							double dist = (getExplosionHitboxGrowth() - entity.getDistance(entity)) > 1 ? (getExplosionHitboxGrowth() - entity.getDistance(entity)) : 1;
							Vec3d velocity = entity.getPositionVector().subtract(this.getPositionVector());
							velocity = velocity.scale((1 / 40F)).scale(dist).add(0, getExplosionHitboxGrowth() / 50, 0);

							double x = velocity.x;
							double y = velocity.y > 0 ? velocity.z : 0.15F;
							double z = velocity.z;
							x *= getKnockbackMult().x;
							y *= getKnockbackMult().y;
							z *= getKnockbackMult().z;

							attackEntity(entity, true);

							if (!entity.world.isRemote) {
								entity.motionX += x;
								entity.motionY += y;
								entity.motionZ += z;
								entity.setFire(getFireTime());

								if (collided instanceof AvatarEntity) {
									if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment)
											&& !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
										AvatarEntity avent = (AvatarEntity) collided;
										avent.addVelocity(x, y, z);
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
	}**/
	/*static void applyPiercingCollision(AvatarEntity entity) {
		List<Entity> collided = world.getEntitiesInAABBexcluding(entity, getExpandedHitbox(), entity -> entity != getOwner());
		if (!collided.isEmpty()) {
			for (Entity entity : collided) {
				if (entity != getOwner() && entity != null && getOwner() != null) {
					attackEntity(entity, false);
				}
			}

		}
	}

	static void Dissipate() {
		if (world instanceof WorldServer) {
			if (getOwner() != null) {
				WorldServer World = (WorldServer) world;
				World.spawnParticle(getParticle(), posX, posY, posZ, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
				world.playSound(null, posX, posY, posZ, getSound(), getSoundCategory(), getVolume(),
						getPitch());
			}
		}
	}

	static void attackEntity(Entity hit, boolean explosionDamage) {
		if (getOwner() != null && hit != null) {
			boolean ds = hit.attackEntityFrom(getDamageSource(hit), explosionDamage ? getAoeDamage() : getDamage());
			if (!ds && hit instanceof EntityDragon) {
				((EntityDragon) hit).attackEntityFromPart(((EntityDragon) hit).dragonPartBody, getDamageSource(hit),
						explosionDamage ? getAoeDamage() : getDamage());
				BattlePerformanceScore.addScore(getOwner(), getPerformanceAmount());

			} else if (hit instanceof EntityLivingBase && ds) {
				BattlePerformanceScore.addScore(getOwner(), getPerformanceAmount());
			}
		}
	}



	static float getAoeDamage() {
		return 1;
	}

	static Vec3d getKnockbackMult() {
		return new Vec3d(1, 1, 1);
	}

	static EnumParticleTypes getParticle() {
		return AvatarParticles.getParticleFlames();
	}

	static int getNumberofParticles() {
		return 50;
	}

	static double getParticleSpeed() {
		return 0.02;
	}

	static int getPerformanceAmount() {
		return 10;
	}

	protected SoundEvent getSound() {
		return SoundEvents.ENTITY_GHAST_SHOOT;
	}

	protected float getVolume() {
		return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
	}

	protected float getPitch() {
		return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
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

	protected boolean shouldDissipate() {
		return false;
	}

	protected boolean shouldExplode() {
		return true;
	}

	static AxisAlignedBB getExpandedHitbox() {
		return expandedHitbox;
	}

	static double getExplosionHitboxGrowth() {
		return 1;
	}

	static void applyElementalContact(AvatarEntity entity) {

	}
**/

}
