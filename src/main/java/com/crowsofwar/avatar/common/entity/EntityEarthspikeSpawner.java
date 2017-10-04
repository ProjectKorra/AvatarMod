package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityEarthspikeSpawner extends AvatarEntity {
    private Vector initialPosition;


    private float damageMult;
    private double maxTravelDistanceSq;
    private boolean breakBlocks;
    private boolean dropEquipment;

    /**
     * @param world
     */
    public EntityEarthspikeSpawner(World world) {
        super(world);
        setSize(1, 1);
        this.damageMult = 1.6F;

    }

    public void setDamageMult(float mult) {
        this.damageMult = mult;
    }

    public void setDistance(double dist) {
        maxTravelDistanceSq = dist * dist;
    }

    public void setBreakBlocks(boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
    }

    public void setDropEquipment(boolean dropEquipment) {
        this.dropEquipment = dropEquipment;
    }

    public double getSqrDistanceTravelled() {
        return position().sqrDist(initialPosition);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        setDead();
    }

    @Override
    public void onEntityUpdate() {

        super.onEntityUpdate();

        if (initialPosition == null) {
            initialPosition = position();
        }

        Vector position = position();
        Vector velocity = velocity();

        setPosition(position.plus(velocity.times(0.0000005)));

        if (!world.isRemote && getSqrDistanceTravelled() > maxTravelDistanceSq) {
            setDead();
        }

        BlockPos above = getPosition().offset(EnumFacing.UP);
        BlockPos below = getPosition().offset(EnumFacing.DOWN);
        Block belowBlock = world.getBlockState(below).getBlock();

        if (ticksExisted % 3 == 0) world.playSound(posX, posY, posZ,
                world.getBlockState(below).getBlock().getSoundType().getBreakSound(),
                SoundCategory.PLAYERS, 1, 1, false);
        if (ticksExisted % 3 == 0 && !world.isRemote) {
            EntityEarthSpike earthspike = new EntityEarthSpike(world);
            earthspike.posX = this.posX;
            earthspike.posY = this.posY;
            earthspike.posZ = this.posZ;

            world.spawnEntity(earthspike);

        }

        if (!world.getBlockState(below).isNormalCube()) {
            setDead();
        }

        if (!world.isRemote && !ConfigStats.STATS_CONFIG.bendableBlocks.contains(belowBlock)) {
            setDead();
        }

        // Destroy if in a block
       IBlockState inBlock = world.getBlockState(getPosition());
        if (inBlock.isFullBlock()) {
            setDead();
        }


        // Destroy non-solid blocks in the ravine
        BlockPos inPos = getPosition();
        if (inBlock.getBlock() != Blocks.AIR && !inBlock.isFullBlock()) {

            if (inBlock.getBlockHardness(world, getPosition()) == 0) {

                breakBlock(getPosition());

            } else {

                setDead();

            }

        }

        // amount of entities which were successfully attacked
        int attacked = 0;

        // Push collided entities back
        if (!world.isRemote) {
            List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
                    entity -> entity != getOwner());
            if (!collided.isEmpty()) {
                for (Entity entity : collided) {
                    if (attackEntity(entity)) {
                        attacked++;
                    }
                }
            }
        }

        if (!world.isRemote && getOwner() != null) {
            BendingData data = BendingData.get(getOwner());
            if (data != null) {
                data.getAbilityData("earthspike").addXp(SKILLS_CONFIG.ravineHit * attacked);
            }
        }

        if (!world.isRemote && breakBlocks) {
            BlockPos last = new BlockPos(prevPosX, prevPosY, prevPosZ);
            if (!last.equals(getPosition()) && !last.equals(initialPosition.toBlockPos())) {

                world.destroyBlock(last.down(), true);

                double travel = Math.sqrt(getSqrDistanceTravelled() / maxTravelDistanceSq);
                double chance = -(travel - 0.5) * (travel - 0.5) + 0.25;
                chance *= 2;

                if (rand.nextDouble() <= chance) {
                    world.destroyBlock(last.down(2), true);
                }

            }
        }

    }

    @Override
    public boolean onCollideWithSolid() {
        setDead();
        return false;
    }

    private boolean attackEntity(Entity entity) {

        if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {

            Vector push = velocity().withY(.8).times(STATS_CONFIG.ravineSettings.push);
            entity.addVelocity(push.x(), push.y(), push.z());
            AvatarUtils.afterVelocityAdded(entity);

            if (dropEquipment && entity instanceof EntityLivingBase) {

                EntityLivingBase elb = (EntityLivingBase) entity;

                for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {

                    ItemStack stack = elb.getItemStackFromSlot(slot);
                    if (!stack.isEmpty()) {
                        double chance = slot.getSlotType() == EntityEquipmentSlot.Type.HAND ? 40 : 20;
                        if (rand.nextDouble() * 100 <= chance) {
                            elb.entityDropItem(stack, 0);
                            elb.setItemStackToSlot(slot, ItemStack.EMPTY);
                        }
                    }

                }

            }
            DamageSource ds = AvatarDamageSource.causeRavineDamage(entity, getOwner());
            float damage = STATS_CONFIG.ravineSettings.damage * damageMult;
            return entity.attackEntityFrom(ds, damage);


        }

        return false;

    }

}


