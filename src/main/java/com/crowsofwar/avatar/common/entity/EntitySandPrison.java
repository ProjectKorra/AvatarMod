/*
  This file is part of AvatarMod.

  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public LSandnse as published by
  the Free Software Foundation, either version 3 of the LSandnse, or
  (at your option) any later version.

  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public LSandnse for more details.

  You should have received a copy of the GNU General Public LSandnse
  along with AvatarMod. If not, see <http://www.gnu.org/lSandnses/>.
*/
package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.sand.Sandbending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.entity.data.SyncedEntity;
import com.google.common.base.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class EntitySandPrison extends AvatarEntity {

    public static final DataParameter<Optional<UUID>> SYNC_IMPRISONED = EntityDataManager
            .createKey(EntitySandPrison.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    public static final DataParameter<Integer> SYNC_IMPRISONED_TIME = EntityDataManager.createKey
            (EntitySandPrison.class, DataSerializers.VARINT);

    public static final DataParameter<Integer> SYNC_MAX_IMPRISONED_TIME = EntityDataManager
            .createKey(EntitySandPrison.class, DataSerializers.VARINT);

    private double normalBaseValue;
    private final SyncedEntity<EntityLivingBase> imprisonedAttr;

    private boolean damageEntity;
    private boolean applySlowness;
    private boolean vulnerableToAirbending;

    /**
     * @param world
     */
    public EntitySandPrison(World world) {
        super(world);
        imprisonedAttr = new SyncedEntity<>(this, SYNC_IMPRISONED);
        setSize(1, 0.25f);
        vulnerableToAirbending = true;
    }

    public static boolean isImprisoned(EntityLivingBase entity) {

        return getPrison(entity) != null;

    }

    /**
     * Get the prison holding that entity, or null if the entity is not
     * imprisoned
     */
    public static EntitySandPrison getPrison(EntityLivingBase entity) {

        World world = entity.world;
        List<EntitySandPrison> prisons = world.getEntities(EntitySandPrison.class,
                prison -> prison.getImprisoned() == entity);

        return prisons.isEmpty() ? null : prisons.get(0);

    }

    public static void imprison(EntityLivingBase entity, EntityLivingBase owner, Ability ab) {
        World world = entity.world;
        EntitySandPrison prison = new EntitySandPrison(world);
        prison.setImprisoned(entity);
        prison.copyLocationAndAnglesFrom(entity);
        prison.setAbility(ab);

        double powerRating = Bender.get(owner).calcPowerRating(Sandbending.ID);
        prison.setStats(AbilityData.get(owner, "sand_prison"), powerRating);

        if (!world.isRemote)
            world.spawnEntity(prison);

        BattlePerformanceScore.addLargeScore(owner);

    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_IMPRISONED, Optional.absent());
        dataManager.register(SYNC_IMPRISONED_TIME, 100);
        dataManager.register(SYNC_MAX_IMPRISONED_TIME, 100);
    }

    public EntityLivingBase getImprisoned() {
        return imprisonedAttr.getEntity();
    }

    public void setImprisoned(EntityLivingBase entity) {
        imprisonedAttr.setEntity(entity);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void onUpdate() {
        //super.onUpdate();
        //Otherwise stuff collides with it
        EntityLivingBase imprisoned = getImprisoned();
        if (imprisoned != null) {
            IAttributeInstance speed = imprisoned.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (speed.getBaseValue() != 0) {
                normalBaseValue = speed.getBaseValue();
                speed.setBaseValue(0);
            }
            imprisoned.motionX *= 0;
            imprisoned.motionY *= 0;
            imprisoned.motionZ *= 0;
            this.motionX = this.motionY = this.motionZ = 0;
            imprisoned.setPositionAndUpdate(posX, posY, posZ);
        }

        if (!world.isRemote) {
            setImprisonedTime(getImprisonedTime() - 1);
        }

        if (getImprisonedTime() <= 0) {
            setDead();

            if (!world.isRemote && imprisoned != null) {
                world.playSound(null, imprisoned.getPosition(),
                        SoundEvents.BLOCK_SAND_BREAK, SoundCategory.PLAYERS, 1, 1);

                if (damageEntity) {
                    DamageSource ds = AvatarDamageSource.causeSandPrisonDamage(imprisoned, getOwner());
                    imprisoned.attackEntityFrom(ds, 8);
                }

                if (applySlowness) {

                    Potion slowness = Potion.getPotionFromResourceLocation("slowness");
                    //noinspection ConstantConditions
                    imprisoned.addPotionEffect(new PotionEffect(slowness, 80, 1));

                }

            }

        }
    }

    @Override
    public boolean onAirContact() {
        if (!world.isRemote && vulnerableToAirbending) {
            setDead();
            return true;
        }
        return false;
    }

    @Override
    public void setDead() {
        super.setDead();
        EntityLivingBase imprisoned = getImprisoned();
        if (imprisoned != null) {
            IAttributeInstance speed = imprisoned.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            speed.setBaseValue(normalBaseValue);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        imprisonedAttr.readFromNbt(nbt);
        normalBaseValue = nbt.getDouble("NormalSpeed");
        setImprisonedTime(nbt.getInteger("ImprisonedTime"));
        setMaxImprisonedTime(nbt.getInteger("MaxImprisonedTime"));
        setVulnerableToAirbending(nbt.getBoolean("VulnerableToAirbending"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        imprisonedAttr.writeToNbt(nbt);
        nbt.setDouble("NormalSpeed", normalBaseValue);
        nbt.setInteger("ImprisonedTime", getImprisonedTime());
        nbt.setInteger("MaxImprisonedTime", getMaxImprisonedTime());
        nbt.setBoolean("VulnerableToAirbending", isVulnerableToAirbending());
    }

    /**
     * A countdown which returns the ticks left to be imprisoned. When the countdown is over, the
     * entity will be freed.
     */
    public int getImprisonedTime() {
        return dataManager.get(SYNC_IMPRISONED_TIME);
    }

    public void setImprisonedTime(int imprisonedTime) {
        dataManager.set(SYNC_IMPRISONED_TIME, imprisonedTime);
    }

    /**
     * Returns the total ticks that the target will be imprisoned for.
     */
    public int getMaxImprisonedTime() {
        return dataManager.get(SYNC_MAX_IMPRISONED_TIME);
    }

    public void setMaxImprisonedTime(int maxImprisonedTime) {
        dataManager.set(SYNC_MAX_IMPRISONED_TIME, maxImprisonedTime);
    }

    public boolean isDamageEntity() {
        return damageEntity;
    }

    public void setDamageEntity(boolean damageEntity) {
        this.damageEntity = damageEntity;
    }

    public boolean isApplySlowness() {
        return applySlowness;
    }

    public void setApplySlowness(boolean applySlowness) {
        this.applySlowness = applySlowness;
    }

    public boolean isVulnerableToAirbending() {
        return vulnerableToAirbending;
    }

    public void setVulnerableToAirbending(boolean vulnerableToAirbending) {
        this.vulnerableToAirbending = vulnerableToAirbending;
    }

    private void setStats(AbilityData abilityData, double powerRating) {

        float imprisonedSeconds = abilityData.getLevel() >= 1 ? 5 : 4;
        damageEntity = false;
        applySlowness = abilityData.getLevel() >= 2;
        vulnerableToAirbending = true;

        if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
            damageEntity = true;
            imprisonedSeconds = 6;
        }
        if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
            imprisonedSeconds = 12;
            vulnerableToAirbending = false;
        }

        imprisonedSeconds += powerRating / 50;

        setImprisonedTime((int) (imprisonedSeconds * 20));
        setMaxImprisonedTime((int) (imprisonedSeconds * 20));

    }

    @Override
    public boolean canPush() {
        return false;
    }

}
