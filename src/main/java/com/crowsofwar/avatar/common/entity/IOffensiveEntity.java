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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;

public interface IOffensiveEntity {

    //TODO: Proper methods for shockwaves!

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
                        //further the closer they are to the player/entity.
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
                    }
                }
            }

        }
        entity.setDead();
    }


    default void applyPiercingCollision(AvatarEntity entity) {
        List<Entity> collided = entity.world.getEntitiesInAABBexcluding(entity, getExpandedHitbox(entity), entity1 -> entity1 != entity.getOwner() &&
                entity1 != entity && entity.canCollideWith(entity1));
        if (!collided.isEmpty()) {
            for (Entity hit : collided) {
                if (entity.getOwner() != null && hit != entity.getOwner() && hit != null && entity.canCollideWith(hit)) {
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
        if (attacker.getOwner() != null && hit != null && hit != attacker && !attacker.world.isRemote) {
            AbilityData data = AbilityData.get(attacker.getOwner(), attacker.getAbility().getName());
            if ((explosionDamage ? getAoeDamage() > 0 : getDamage() > 0) && attacker.canDamageEntity(hit)) {
                boolean ds = hit.attackEntityFrom(getDamageSource(hit, attacker.getOwner()), explosionDamage ? getAoeDamage() : getDamage());
                if (data != null) {
                    if (!ds && hit instanceof EntityDragon) {
                        ((EntityDragon) hit).attackEntityFromPart(((EntityDragon) hit).dragonPartBody, getDamageSource(hit, attacker.getOwner()),
                                explosionDamage ? getAoeDamage() : getDamage());
                        BattlePerformanceScore.addScore(attacker.getOwner(), getPerformanceAmount());
                        if (multiHit())
                            ((EntityLivingBase) hit).hurtTime = 1;
                        data.addXp(getXpPerHit());

                    } else if (ds) {
                        BattlePerformanceScore.addScore(attacker.getOwner(), getPerformanceAmount());
                        data.addXp(getXpPerHit());
                        hit.setFire(getFireTime());
                        if (setVelocity())
                            AvatarUtils.setVelocity(hit, vel);
                        else hit.addVelocity(vel.x, vel.y, vel.z);
                        if (multiHit() && hit instanceof EntityLivingBase)
                            ((EntityLivingBase) hit).hurtTime = 1;
                        AvatarUtils.afterVelocityAdded(hit);
                    }
                }
            } else if (attacker.canCollideWith(hit)) {
                if (hit instanceof EntityItem)
                    vel = vel.scale(0.05);
                BattlePerformanceScore.addScore(attacker.getOwner(), getPerformanceAmount());
                data.addXp(getXpPerHit());
                hit.setFire(getFireTime());
                if (hit instanceof EntityOffensive)
                    ((EntityOffensive) hit).applyElementalContact(attacker);
                if (setVelocity())
                    AvatarUtils.setVelocity(hit, vel);
                else {
                    if (hit instanceof EntityArrow || hit instanceof EntityThrowable)
                        hit.addVelocity(vel.x / 5, vel.y / 2.5, vel.z / 5);
                    else hit.addVelocity(vel.x, vel.y, vel.z);
                }
                if (multiHit() && hit instanceof EntityLivingBase)
                    ((EntityLivingBase) hit).hurtTime = 1;
                AvatarUtils.afterVelocityAdded(hit);
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

    default Vec3d getKnockback(Entity target) {
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
        if (getSounds() != null)
            for (int i = 0; i < getSounds().length; i++)
                entity.world.playSound(null, new BlockPos(entity), getSounds()[i],
                        entity.getSoundCategory(), getPitch(), getVolume());
    }

    //Only called when a piercing projectile hits an entity.
    default void playPiercingSounds(Entity entity) {
        if (getSounds() != null)
            for (int i = 0; i < getSounds().length; i++)
                entity.world.playSound(null, new BlockPos(entity), getSounds()[i],
                        entity.getSoundCategory(), getPitch(), getVolume());
    }

    default void playDissipateSounds(Entity entity) {
        if (getSounds() != null)
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

    @Nullable
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

    default void setDamageSource(String source) {

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

    //This denotes whether the entity is like a shockwave (expanding hitbox), or like a projectile/still spike-like entity.
    //Used for determining which collision methods to use.
    default boolean isShockwave() {
        return false;
    }

    //NOTE: shouldDissipate is checked first when timing out or when an entity has noClip is is colliding with a block.
    //Otherwise, shouldExplode takes precedence over shouldDissipate.
    default boolean shouldDissipate() {
        return false;
    }

    default boolean shouldExplode() {
        return true;
    }

    //If this is true, entities will multihit (add knockback and/or attack even when an entity's hurt timer isn't 0)
    default boolean multiHit() {
        return false;
    }

    default AxisAlignedBB getExpandedHitbox(AvatarEntity entity) {
        return entity.getEntityBoundingBox().grow(getExpandedHitboxWidth(), getExpandedHitboxHeight(), getExpandedHitboxWidth());
    }

    default double getExplosionHitboxGrowth() {
        return 1;
    }

    default boolean setVelocity() {
        return false;
    }

    default void applyElementalContact(AvatarEntity entity) {
    }

}
