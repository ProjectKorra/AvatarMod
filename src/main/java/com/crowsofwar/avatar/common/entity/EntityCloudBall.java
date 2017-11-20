package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.air.CloudburstPowerModifier;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.PlayerBender;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import static com.google.gson.internal.bind.TypeAdapters.UUID;

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
    private boolean absorbtion;
    private boolean chismash;

    /**
     * @param world
     */
   public EntityCloudBall(World world) {
        super(world);
        setSize(0.8f, 0.8f);

    }
    public void canAbsorb (boolean canAbsorb) {
        this.absorbtion = canAbsorb;
    }
    public void canchiSmash (boolean canchiSmash) {
       this.chismash = canchiSmash;
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
        if (ticksExisted >= 250 && this.getBehavior() instanceof CloudburstBehavior.Thrown) {
            this.setDead();
        }

      /*  if (!world.isRemote){
            Thread.dumpStack();
        }**/

        // TODO Temporary fix to avoid extra fireballs
        // Add hook or something
        if (getOwner() == null) {
            setDead();
            removeStatCtrl();
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

        if (getOwner() != null) {
            AbilityData abilityData = BendingData.get(getOwner()).getAbilityData("cloudburst");
            abilityData.addXp(3);

        }

        setDead();
        return true;

    }

    @Override
    protected boolean canCollideWith(Entity entity) {
        if (getOwner() != null) {


            if (absorbtion) {
                if (entity instanceof AvatarEntity) {
                    ((AvatarEntity) entity).isProjectile();
                    entity.setDead();
                    damage += 3F;
                    return false;
                }


                if (entity instanceof EntityArrow) {
                    entity.setDead();
                    damage += 2F;
                    return false;
                }
                if (entity instanceof EntityThrowable) {
                    entity.setDead();
                    damage += 1F;
                    return false;
                }
            }
            if (chismash){
                if (entity instanceof EntityLivingBase) {
                    if (Bender.isBenderSupported((EntityLivingBase) entity)) {
                        BendingData data = BendingData.get((EntityLivingBase) entity);
                        CloudburstPowerModifier cloudModifier = new CloudburstPowerModifier();
                        cloudModifier.setTicks(100);
                        //for (UUID uuid : data.getActiveBendingId()) {
                          //  data.getPowerRatingManager(uuid).addModifier(cloudModifier);
                        //}

                    }
                }
            }

        }


        return super.canCollideWith(entity) || entity instanceof EntityLivingBase;

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

    @Override
    public boolean isProjectile() {
        return true;
    }

}




