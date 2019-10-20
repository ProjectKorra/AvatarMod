package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
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

	default void Explode(World world, AvatarEntity entity, EntityLivingBase owner) {
		if (owner != null) {
			if (world.isRemote)
				spawnExplosionParticles();
			//TODO: Get rid of this particle spawning code. It's outdated.
			if (world instanceof WorldServer) {
				WorldServer World = (WorldServer) world;
				World.spawnParticle(getParticle(), entity.posX, entity.posY, entity.posZ, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
			}
			for (int i = 0; i < getSounds().length; i++) {
				entity.playSound(getSounds()[i], getVolume(),
						getPitch());
			}
			List<Entity> collided = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().grow(getExplosionHitboxGrowth(),
					getExplosionHitboxGrowth(), getExplosionHitboxGrowth()),
					entity1 -> entity1 != entity.getOwner());

			if (!collided.isEmpty()) {
				for (Entity entity1 : collided) {
					if (entity.getOwner() != null && entity1 != entity.getOwner() && entity1 != entity && !world.isRemote) {
						attackEntity(entity, entity1, false, new Vec3d(getKnockback().x * getKnockbackMult().x, getKnockback().y
								* getKnockbackMult().y, getKnockback().z * getKnockbackMult().z));
						//Divide the result of the position difference to make entities fly
						//further the closer they are to the player.
						double dist = (getExplosionHitboxGrowth() - entity1.getDistance(entity)) > 1 ? (getExplosionHitboxGrowth() - entity1.getDistance(entity)) : 1;
						Vec3d velocity = entity1.getPositionVector().subtract(entity.getPositionVector());
						velocity = velocity.scale((1 / 40F)).scale(dist).add(0, getExplosionHitboxGrowth() / 50, 0);

						double x = velocity.x;
						double y = velocity.y > 0 ? velocity.z : 0.15F;
						double z = velocity.z;
						x *= getExplosionKnockbackMult().x;
						y *= getExplosionKnockbackMult().y;
						z *= getExplosionKnockbackMult().z;

						attackEntity(entity, entity1, true, new Vec3d(x, y, z));

						entity1.motionX += x;
						entity1.motionY += y;
						entity1.motionZ += z;
						entity1.setFire(getFireTime());

						if (collided instanceof AvatarEntity) {
							if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment)
									&& !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
								AvatarEntity avent = (AvatarEntity) collided;
								avent.addVelocity(x, y, z);
							}
							entity1.isAirBorne = true;
							AvatarUtils.afterVelocityAdded(entity1);
						}
					}
				}
			}

		}
		entity.setDead();
	}


	default void applyPiercingCollision(AvatarEntity entity) {
		List<Entity> collided = entity.world.getEntitiesInAABBexcluding(entity, getExpandedHitbox(entity), entity1 -> entity1 != entity.getOwner() &&
				entity1 != entity);
		if (!collided.isEmpty()) {
			for (Entity hit : collided) {
				if (entity.getOwner() != null && hit != entity.getOwner() && hit != null) {
					attackEntity(entity, hit, false, getKnockback().crossProduct(getKnockbackMult()));
				}
			}
		}
		if (entity.world.isRemote)
			spawnPiercingParticles();
	}

	default void Dissipate(AvatarEntity entity) {
		if (entity.getOwner() != null) {
			if (entity.world.isRemote)
				//ParticleBuilder particles.
				spawnDissipateParticles();
			if (entity.world instanceof WorldServer) {
				WorldServer World = (WorldServer) entity.world;
				World.spawnParticle(getParticle(), entity.posX, entity.posY, entity.posZ, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
			}
			for (int i = 0; i < getSounds().length; i++) {
				entity.playSound(getSounds()[i], getVolume(),
						getPitch());
			}
		}
		entity.setDead();
	}

	default void attackEntity(AvatarEntity attacker, Entity hit, boolean explosionDamage, Vec3d vel) {
		if (attacker.getOwner() != null && hit != null && hit != attacker) {
			boolean ds = hit.attackEntityFrom(getDamageSource(hit, attacker.getOwner()), explosionDamage ? getAoeDamage() : getDamage());
			AbilityData data = AbilityData.get(attacker.getOwner(), attacker.getAbility().getName());
			if (data != null) {
				if (!ds && hit instanceof EntityDragon) {
					((EntityDragon) hit).attackEntityFromPart(((EntityDragon) hit).dragonPartBody, getDamageSource(hit, attacker.getOwner()),
							explosionDamage ? getAoeDamage() : getDamage());
					BattlePerformanceScore.addScore(attacker.getOwner(), getPerformanceAmount());
					data.addXp(getXpPerHit());
					hit.setEntityInvulnerable(false);

				} else if (hit instanceof EntityLivingBase && ds) {
					BattlePerformanceScore.addScore(attacker.getOwner(), getPerformanceAmount());
					data.addXp(getXpPerHit());
					hit.setFire(getFireTime());
					hit.addVelocity(vel.x, vel.y, vel.z);
					hit.setEntityInvulnerable(false);
					AvatarUtils.afterVelocityAdded(hit);
				}
			}
		}
	}

	default float getAoeDamage() {
		return 1;
	}

	default float getDamage() {
		return 3;
	}

	default float getXpPerHit() {
		return 3;
	}

	default Vec3d getKnockback() {
		return Vec3d.ZERO;
	}

	default Vec3d getKnockbackMult() {
		return new Vec3d(1, 1, 1);
	}

	default Vec3d getExplosionKnockbackMult() {
		return new Vec3d(0.5, 0.5, 0.5);
	}

	//You don't have to do a world.isRemote check, it's done for you.
	//Self-explanatory particle spawning methods.
	default void spawnExplosionParticles() {}

	default void spawnDissipateParticles() {}

	default void spawnPiercingParticles() {}

	//TODO: Get rid of these particle methods as they're unnecessary with the ease of the new particle system.
	default EnumParticleTypes getParticle() {
		return AvatarParticles.getParticleFlames();
	}

	default int getNumberofParticles() {
		return 50;
	}

	default double getParticleSpeed() {
		return 0.02;
	}


	default int getPerformanceAmount() {
		return 10;
	}

	default SoundEvent[] getSounds() {
		SoundEvent[] events = new SoundEvent[1];
		events[0] = SoundEvents.ENTITY_GHAST_SHOOT;
		return events;
	}

	default float getVolume() {
		return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
	}

	default float getPitch() {
		return 1.0F + AvatarUtils.getRandomNumberInRange(1, 100) / 500F;
	}

	default DamageSource getDamageSource(Entity target, EntityLivingBase owner) {
		return AvatarDamageSource.causeFireDamage(target, owner);
	}

	default double getExpandedHitboxWidth() {
		return 0.25;
	}

	default double getExpandedHitboxHeight() {
		return 0.25;
	}

	default int getFireTime() {
		return 0;
	}

	default boolean isPiercing() {
		return false;
	}

	//NOTE: IOffensiveEntities will dissipate when their lifetime is up even when this is false, unless overridden.
	default boolean shouldDissipate() {
		return false;
	}

	default boolean shouldExplode() {
		return true;
	}

	default AxisAlignedBB getExpandedHitbox(AvatarEntity entity) {
		return entity.getEntityBoundingBox().grow(getExpandedHitboxWidth(), getExpandedHitboxHeight(), getExpandedHitboxWidth());
	}

	default double getExplosionHitboxGrowth() {
		return 1;
	}

	default void applyElementalContact(AvatarEntity entity) {

	}

}
