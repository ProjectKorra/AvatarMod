package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public interface IOffensiveEntity {

	default void Explode(World world, AvatarEntity entity, EntityLivingBase owner) {
		if (owner != null) {
			//Having a general method means you can use either particle system! Hooray!
			spawnExplosionParticles(world, AvatarEntityUtils.getMiddleOfEntity(entity));
			playExplosionSounds(entity);
			List<Entity> collided = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().grow(getExplosionHitboxGrowth(),
					getExplosionHitboxGrowth(), getExplosionHitboxGrowth()),
					entity1 -> entity1 != entity.getOwner());

			if (!collided.isEmpty()) {
				for (Entity entity1 : collided) {
					if (entity.getOwner() != null && entity1 != entity.getOwner() && entity1 != entity && !world.isRemote) {

						attackEntity(entity, entity1, false, getKnockback());
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
					attackEntity(entity, hit, false, getKnockback());
				}
			}
		}
		playPiercingSounds(entity);
		spawnPiercingParticles(entity.world, AvatarEntityUtils.getMiddleOfEntity(entity));
	}

	default void Dissipate(AvatarEntity entity) {
		if (entity.getOwner() != null) {
			spawnDissipateParticles(entity.world, AvatarEntityUtils.getMiddleOfEntity(entity));
			playDissipateSounds(entity);
		}
		entity.setDead();
	}

	default void attackEntity(AvatarEntity attacker, Entity hit, boolean explosionDamage, Vec3d vel) {
		if (attacker.getOwner() != null && hit != null && hit != attacker) {
			AbilityData data = AbilityData.get(attacker.getOwner(), attacker.getAbility().getName());
			if ((explosionDamage ? getAoeDamage() > 0 : getDamage() > 0) && attacker.canDamageEntity(hit)) {
				boolean ds = hit.attackEntityFrom(getDamageSource(hit, attacker.getOwner()), explosionDamage ? getAoeDamage() : getDamage());
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
			} else if (attacker.canCollideWith(hit) && hit.canBeAttackedWithItem()){
				if (hit instanceof EntityLivingBase) {
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
		return new Vec3d(getKnockbackMult().x, getKnockbackMult().y, getKnockbackMult().z);
	}

	default Vec3d getKnockbackMult() {
		return new Vec3d(1, 1, 1);
	}

	default Vec3d getExplosionKnockbackMult() {
		return new Vec3d(0.5, 0.5, 0.5);
	}

	//You do have to do a server or client side check, depending on what you want to spawn.
	//Self-explanatory particle spawning methods.
	default void spawnExplosionParticles(World world, Vec3d pos) {
		if (world instanceof WorldServer && !world.isRemote) {
			WorldServer World = (WorldServer) world;
			if (getParticle() != null)
				World.spawnParticle(getParticle(), pos.x, pos.y, pos.z, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
		}
	}

	default void spawnDissipateParticles(World world, Vec3d pos) {
		if (world instanceof WorldServer && !world.isRemote) {
			WorldServer World = (WorldServer) world;
			if (getParticle() != null)
				World.spawnParticle(getParticle(), pos.x, pos.y, pos.z, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
		}
	}

	default void spawnPiercingParticles(World world, Vec3d pos) {
		if (world instanceof WorldServer && !world.isRemote) {
			WorldServer World = (WorldServer) world;
			if (getParticle() != null)
				World.spawnParticle(getParticle(), pos.x, pos.y, pos.z, getNumberofParticles(), 0, 0, 0, getParticleSpeed());
		}
	}

	default void playExplosionSounds(Entity entity) {
		for (int i = 0; i < getSounds().length; i++)
			entity.world.playSound(null, new BlockPos(entity), getSounds()[i],
					entity.getSoundCategory(), getPitch(), getVolume());
	}

	//Only called when a piercing projectile hits an entity.
	default void playPiercingSounds(Entity entity) {
		for (int i = 0; i < getSounds().length; i++)
			entity.world.playSound(null, new BlockPos(entity), getSounds()[i],
					entity.getSoundCategory(), getPitch(), getVolume());
	}

	default void playDissipateSounds(Entity entity) {
		for (int i = 0; i < getSounds().length; i++)
			entity.world.playSound(null, new BlockPos(entity), getSounds()[i],
					entity.getSoundCategory(), getPitch(), getVolume());
	}

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

	default void applyElementalContact(AvatarEntity entity) {}

}
