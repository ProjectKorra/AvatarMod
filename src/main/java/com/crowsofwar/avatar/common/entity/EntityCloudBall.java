package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityCloudBall extends AvatarEntity {
    /**
     * @param world
     */
    public static final DataParameter<CloudburstBehavior> SYNC_BEHAVIOR = EntityDataManager
            .createKey(EntityCloudBall.class, CloudburstBehavior.DATA_SERIALIZER);

    public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityCloudBall.class,
            DataSerializers.VARINT);

    private AxisAlignedBB expandedHitbox;

    private float damage;

    /**
     * @param world
     */
   public EntityCloudBall(World world) {
        super(world);
        setSize(0.8f, 0.8f);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_BEHAVIOR, new CloudburstBehavior.Idle());
        dataManager.register(SYNC_SIZE, 30);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        setBehavior((CloudburstBehavior) getBehavior().onUpdate(this));

        // TODO Temporary fix to avoid extra fireballs
        // Add hook or something
        if (getOwner() == null) {
            setDead();
        }

    }



    public CloudburstBehavior getBehavior() {
        return dataManager.get(SYNC_BEHAVIOR);
    }

    public void setBehavior(CloudburstBehavior behavior) {
        dataManager.set(SYNC_BEHAVIOR, behavior);
    }

    @Override
    public EntityLivingBase getController() {
        return getBehavior() instanceof CloudburstBehavior.PlayerControlled ? getOwner() : null;
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
        boolean indestructible = false;

        if (getOwner() != null) {
            AbilityData abilityData = BendingData.get(getOwner())
                    .getAbilityData("cloudburst");
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                indestructible = true;
                if (indestructible = true){
                    setDead();
                    return false;
                }

                }
            }




        setDead();
        return true;

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setDamage(nbt.getFloat("Damage"));
        setBehavior((CloudburstBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
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




