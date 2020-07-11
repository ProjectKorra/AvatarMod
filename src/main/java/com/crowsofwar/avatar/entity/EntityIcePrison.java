/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.ice.Icebending;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.entity.data.SyncedEntity;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityIcePrison extends AvatarEntity {

    public static final DataParameter<Optional<UUID>> SYNC_IMPRISONED = EntityDataManager
            .createKey(EntityIcePrison.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    public static final DataParameter<Integer> SYNC_IMPRISONED_TIME = EntityDataManager.createKey
            (EntityIcePrison.class, DataSerializers.VARINT);

    public static final DataParameter<Integer> SYNC_MAX_IMPRISONED_TIME = EntityDataManager
            .createKey(EntityIcePrison.class, DataSerializers.VARINT);

    private double normalBaseValue;
    private final SyncedEntity<EntityLivingBase> imprisonedAttr;

    private boolean meltInSun;
    private boolean meltInFire;
    private boolean attackOnce;
    private boolean attackRepeat;

    /**
     * @param world
     */
    public EntityIcePrison(World world) {
        super(world);
        imprisonedAttr = new SyncedEntity<>(this, SYNC_IMPRISONED);
        setSize(3, 4);
    }

    public static boolean isImprisoned(EntityLivingBase entity) {

        return getPrison(entity) != null;

    }

    /**
     * Get the prison holding that entity, or null if the entity is not
     * imprisoned
     */
    public static EntityIcePrison getPrison(EntityLivingBase entity) {

        World world = entity.world;
        List<EntityIcePrison> prisons = world.getEntities(EntityIcePrison.class,
                prison -> prison.getImprisoned() == entity);

        return prisons.isEmpty() ? null : prisons.get(0);

    }

    public static void imprison(EntityLivingBase entity, EntityLivingBase owner, Ability ab) {
        World world = entity.world;
        EntityIcePrison prison = new EntityIcePrison(world);

        prison.setImprisoned(entity);
        prison.setOwner(owner);
        prison.copyLocationAndAnglesFrom(entity);
        prison.setAbility(ab);

        Bender bender = Bender.get(owner);
        prison.setStats(bender.getData().getAbilityData("ice_prison"), bender.calcPowerRating(Icebending.ID));

        if (!world.isRemote)
            world.spawnEntity(prison);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_IMPRISONED, Optional.absent());
        dataManager.register(SYNC_IMPRISONED_TIME, 100);
        dataManager.register(SYNC_MAX_IMPRISONED_TIME, 100);
    }

    @Nullable
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

        // Countdown imprisonedTime
        if (!world.isRemote) {
            setImprisonedTime(getImprisonedTime() - 1);

            // Reduce imprisonedTime 50% faster in the sun
            if (meltInSun) {
                if (world.isDaytime()) {
                    boolean inSky = world.canBlockSeeSky(getPosition());
                    if (inSky && ticksExisted % 2 == 0) {
                        setImprisonedTime(getImprisonedTime() - 1);
                    }
                }
            }

        }

        // Continually damage entity
        if (attackRepeat && !world.isRemote) {
            if (getImprisonedTime() % 20 == 19) {
                attackPrisoner(1);
            }
        }

        if (getImprisonedTime() <= 0) {
            setDead();

            if (!world.isRemote && imprisoned != null) {
                world.playSound(null, imprisoned.posX, imprisoned.posY, imprisoned.posZ,
                        SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1, 1);
                imprisoned.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"),
                        60, 1, false, false));

                if (attackOnce || attackRepeat) {
                    attackPrisoner(2f);
                }

            }

        }

        if (imprisoned == null || imprisoned.isDead) {
            setDead();
        }

    }

    private void attackPrisoner(float damageMultiplier) {
        EntityLivingBase imprisoned = getImprisoned();

        if (imprisoned != null) {
            DamageSource ds = AvatarDamageSource.causeIcePrisonDamage(imprisoned, getOwner());
            imprisoned.attackEntityFrom(ds, STATS_CONFIG.icePrisonDamage * damageMultiplier);
        }
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
    public boolean onFireContact() {
        if (meltInFire && !world.isRemote) {
            setImprisonedTime(getImprisonedTime() - 1);
        }
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        imprisonedAttr.readFromNbt(nbt);
        normalBaseValue = nbt.getDouble("NormalSpeed");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        imprisonedAttr.writeToNbt(nbt);
        nbt.setDouble("NormalSpeed", normalBaseValue);
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

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    /**
     * Sets the statistics of this prison based on that ability data
     */
    private void setStats(AbilityData data, double powerRating) {

        attackOnce = data.getLevel() >= 2;
        attackRepeat = data.isMasterPath(AbilityData.AbilityTreePath.FIRST);
        meltInSun = !data.isMasterPath(AbilityData.AbilityTreePath.SECOND);
        meltInFire = !data.isMasterPath(AbilityData.AbilityTreePath.SECOND);
        double imprisonedSeconds = 3 + data.getLevel() + powerRating / 35f;

        setImprisonedTime((int) (imprisonedSeconds * 20));
        setMaxImprisonedTime(getImprisonedTime());

    }
}
