package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.custom.light.AbilityHolyProtection;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityBuff extends AvatarEntity {
    private static final DataParameter<Integer> SYNC_LIFETIME = EntityDataManager.createKey(
            EntityBuff.class, DataSerializers.VARINT);
    private static final DataParameter<Float> SYNC_RADIUS = EntityDataManager.createKey(
            EntityBuff.class, DataSerializers.FLOAT);

    /**
     * @param world
     */
    public EntityBuff(World world) {
        super(world);
        this.setSize(0.1F, 0.1F);
    }

    public int getLifetime() {
        return dataManager.get(SYNC_LIFETIME);
    }

    public void setLifetime(int lifetime) {
        dataManager.set(SYNC_LIFETIME, lifetime);
    }

    public float getRadius() {
        return dataManager.get(SYNC_RADIUS);
    }

    public void setRadius(float radius) {
        dataManager.set(SYNC_RADIUS, radius);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_LIFETIME, 1);
        dataManager.register(SYNC_RADIUS, 3F);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    protected void collideWithNearbyEntities() {

    }

    @Override
    public void applyEntityCollision(Entity entity) {

    }

    @Override
    public void onCollideWithEntity(Entity entity) {

    }

    @Override
    public boolean onCollideWithSolid() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (ticksExisted >= getLifetime())
            setDead();

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                getEntityBoundingBox().grow(getRadius()));
        if (!entities.isEmpty() && getOwner() != null) {
            if (!world.isRemote) {
                for (EntityLivingBase toBuff : entities) {
                    if (!DamageUtils.canDamage(getOwner(), toBuff)) {
//                        toBuff.addPotionEffect(new PotionEffect(MobEffects.SPEED,
//                                120, 1));
                        toBuff.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,
                                120, 1));
                    }
                }
            }
        }
        if (getAbility() instanceof AbilityHolyProtection) {
            if (getOwner() != null) {
                List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class,
                        getEntityBoundingBox().grow(getRadius()));
                if (!targets.isEmpty()) {
                    for (EntityLivingBase target : targets) {
                        if (!DamageUtils.canDamage(getOwner(), target)) {
                            target.addPotionEffect(new PotionEffect(MobEffects.SPEED,
                                    120, 2));
                            target.addPotionEffect(new PotionEffect(MobEffects.GLOWING,
                                    120, 2));
                            target.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,
                                    120, 0));
                        }
                    }
                }
            }
        }

        if (world.isRemote && getOwner() != null) {
            Vec3d startPos = getPositionVector().add(0, 200, 0);
            if (ticksExisted < 20)
                ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(1.0F, 1.0F, 0.8F)
                        .time(20).pos(startPos).scale(getRadius() / 15 * ticksExisted * 3).target(startPos.subtract(0,
                        ticksExisted * 10, 0)).spawnEntity(getOwner()).spawn(world);
            else if (ticksExisted == 20)
                ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(1.0F, 1.0F, 0.8F)
                        .time(getLifetime() - 20).pos(startPos).scale(getRadius() * 4).target(startPos.subtract(0,
                        200, 0)).spawnEntity(getOwner()).spawn(world);
        }
    }
}
