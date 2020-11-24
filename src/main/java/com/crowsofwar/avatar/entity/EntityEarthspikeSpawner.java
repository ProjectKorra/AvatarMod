package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.config.ConfigStats;
import com.crowsofwar.avatar.entity.data.EarthspikesBehavior;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityEarthspikeSpawner extends EntityOffensive {

    private static final DataParameter<EarthspikesBehavior> SPIKES_BEHAVIOR = EntityDataManager
            .createKey(EntityEarthspikeSpawner.class, EarthspikesBehavior.SERIALIZER);
    private boolean unstoppable;
    private double maxTicksAlive;
    private SpikesType type;
    private double spikeSize;
    private final BlockPos targetPos;

    /**
     * @param world
     */
    public EntityEarthspikeSpawner(World world) {
        super(world);
        setSize(1, 1);
        this.targetPos = new BlockPos(0, 0, 0);
    }

    public SpikesType getType() {
        return type;
    }

    public void setType(SpikesType isType) {
        this.type = isType;
    }

    public boolean getUnstoppable() {
        return unstoppable;
    }

    public void setUnstoppable(boolean isUnstoppable) {
        this.unstoppable = isUnstoppable;
    }

    public double getDuration() {
        return this.maxTicksAlive;
    }

    public void setDuration(double ticks) {
        this.maxTicksAlive = ticks;
    }

    public double getSize() {
        return this.spikeSize;
    }

    public void setSize(double size) {
        this.spikeSize = size;
    }

    @Override
    public float getDamage() {
        return super.getDamage();
    }

    @Override
    public void setDamage(float damage) {
        super.setDamage(damage);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }

    @Override
    public EntityLivingBase getController() {
        return getOwner();
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Nullable
    @Override
    public SoundEvent[] getSounds() {
        return new SoundEvent[0];
    }

    @Override
    public void spawnDissipateParticles(World world, Vec3d pos) {

    }

    @Override
    public void entityInit() {
        super.entityInit();
        dataManager.register(SPIKES_BEHAVIOR, new EarthspikesBehavior.Init());
    }

    public EarthspikesBehavior getBehavior() {
        return dataManager.get(SPIKES_BEHAVIOR);
    }

    public void setBehavior(EarthspikesBehavior behavior) {
        dataManager.set(SPIKES_BEHAVIOR, behavior);
    }

    @Override
    public boolean isPiercing() {
        return true;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (ticksExisted >= maxTicksAlive) {
            Dissipate();
        }

        if (this.getPosition() == targetPos || this.getDistanceSq(targetPos) < 1)
            Dissipate();

        BlockPos below = getPosition().offset(EnumFacing.DOWN);
        Block belowBlock = world.getBlockState(below).getBlock();

        if (ticksExisted % 3 == 0) world.playSound(posX, posY, posZ,
                world.getBlockState(below).getBlock().getSoundType().getBreakSound(),
                SoundCategory.PLAYERS, 1, 1, false);

        if (!world.getBlockState(below).isNormalCube()) {
            setDead();
        }

        if (!world.isRemote && !ConfigStats.STATS_CONFIG.bendableBlocks.contains(belowBlock) && !unstoppable) {
            setDead();
        }

        if (!world.isRemote && belowBlock == Blocks.AIR) {
            setDead();
        }

        // Destroy if in a block
        IBlockState inBlock = world.getBlockState(getPosition());
        if (inBlock.isFullBlock()) {
            setDead();
        }

        // Destroy non-solid blocks in the earthspike
        if (inBlock.getBlock() != Blocks.AIR && !inBlock.isFullBlock()) {
            if (inBlock.getBlockHardness(world, getPosition()) == 0) {
                breakBlock(getPosition());
            } else {
                setDead();
            }
        }

        setBehavior((EarthspikesBehavior) getBehavior().onUpdate(this));
    }

    @Override
    public void setDead() {
        if (getOwner() != null) {
            AttributeModifier modifier = getOwner().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(EarthspikesBehavior.MOVEMENT_MODIFIER_ID);
            if (modifier != null && getOwner().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(modifier)) {
                getOwner().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(EarthspikesBehavior.MOVEMENT_MODIFIER_ID);
            }
        }
        super.setDead();
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
    public BendingStyle getElement() {
        return new Earthbending();
    }

    // Allows setting the spikes type
    public enum SpikesType {
        LINE, OCTOPUS
    }

}
