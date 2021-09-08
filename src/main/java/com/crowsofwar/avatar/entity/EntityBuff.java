package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
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

    public void setRadius(float radius) {
        dataManager.set(SYNC_RADIUS, radius);
    }

    public float getRadius() {
        return dataManager.get(SYNC_RADIUS);
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
                        toBuff.addPotionEffect(new PotionEffect(MobEffects.SPEED,
                                120, 1));
                        toBuff.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,
                                120, 1));
                    }
                }
            }
        }
    }
}
