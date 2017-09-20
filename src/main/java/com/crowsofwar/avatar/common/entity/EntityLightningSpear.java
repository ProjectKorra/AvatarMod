package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.common.entity.data.LightningSpearBehavior;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityLightningSpear extends AvatarEntity {
    /**
     * @param world
     */
    public static final DataParameter<LightningSpearBehavior> SYNC_BEHAVIOR = EntityDataManager
            .createKey(EntityLightningSpear.class, LightningSpearBehavior.DATA_SERIALIZER);

    public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityLightningSpear.class,
            DataSerializers.VARINT);

    private AxisAlignedBB expandedHitbox;

    private float damage;

    /**
     * @param world
     */
    public EntityLightningSpear(World world) {
        super(world);
        setSize(0.8f, 0.8f);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_BEHAVIOR, new LightningSpearBehavior.Idle());
        dataManager.register(SYNC_SIZE, 30);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        setBehavior((LightningSpearBehavior) getBehavior().onUpdate(this));

        // TODO Temporary fix to avoid extra fireballs
        // Add hook or something
        if (getOwner() == null) {
            setDead();
        }

    }



    public LightningSpearBehavior getBehavior() {
        return dataManager.get(SYNC_BEHAVIOR);
    }

    public void setBehavior(LightningSpearBehavior behavior) {
        dataManager.set(SYNC_BEHAVIOR, behavior);
    }

    @Override
    public EntityLivingBase getController() {
        return getBehavior() instanceof LightningSpearBehavior.PlayerControlled ? getOwner() : null;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public int getSize() {
        return dataManager.get(SYNC_SIZE);
    }

    public void setSize(int size) {
        dataManager.set(SYNC_SIZE, size);
    }

    @Override
    public boolean onCollideWithSolid() {

        float explosionSize = STATS_CONFIG.fireballSettings.explosionSize;
        explosionSize *= getSize() / 30f;
        boolean destroyObsidian = false;

        if (getOwner() != null) {
            AbilityData abilityData = BendingData.get(getOwner())
                    .getAbilityData("cloudburst");
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                destroyObsidian = false;
            }
        }
        if (!world.isRemote){

            world.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, 0, 0, 0 );
        }



        setDead();
        return true;

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setDamage(nbt.getFloat("Damage"));
        setBehavior((LightningSpearBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("Damage", getDamage());
        nbt.setInteger("Behavior", getBehavior().getId());
    }


    public AxisAlignedBB getExpandedHitbox() {
        return this.expandedHitbox;
    }

    @Override
    public void setEntityBoundingBox(AxisAlignedBB bb) {
        super.setEntityBoundingBox(bb);
        expandedHitbox = bb.grow(0.35, 0.35, 0.35);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    private void removeStatCtrl() {
        if (getOwner() != null) {
            BendingData data = Bender.get(getOwner()).getData();
            data.removeStatusControl(StatusControl.THROW_CLOUDBURST);
        }
    }

}




