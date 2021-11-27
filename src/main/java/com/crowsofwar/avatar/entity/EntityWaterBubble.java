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

import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.bending.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.blocks.BlockTemp;
import com.crowsofwar.avatar.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class EntityWaterBubble extends EntityOffensive implements IShieldEntity {

    private static final DataParameter<Float> SYNC_HEALTH = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_DEGREES_PER_SECOND = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_MAX_SIZE = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);
    //Using ordinals of the STATE to sync them
    private static final DataParameter<Integer> SYNC_STATE = EntityDataManager.createKey(EntityWaterBubble.class,
            DataSerializers.VARINT);
    /**
     * Whether the water bubble will get a water source upon landing. Only
     * set on server-side.
     */
    private boolean sourceBlock;

    public EntityWaterBubble(World world) {
        super(world);
        setSize(.8f, .8f);
        this.putsOutFires = true;
    }

    //The method in EntityOffensive is used for growing the water bubble.
    //The method here is for charging it.
    public float getMaxSize() {
        return dataManager.get(SYNC_MAX_SIZE);
    }

    public void setMaxSize(float maxSize) {
        dataManager.set(SYNC_MAX_SIZE, maxSize);
    }

    public float getHealth() {
        return dataManager.get(SYNC_HEALTH);
    }

    public void setHealth(float health) {
        dataManager.set(SYNC_HEALTH, health);
    }

    @Override
    public float getMaxHealth() {
        return 0;
    }

    @Override
    public void setMaxHealth(float maxHealth) {

    }

    public float getDegreesPerSecond() {
        return dataManager.get(SYNC_DEGREES_PER_SECOND);
    }

    public void setDegreesPerSecond(float degrees) {
        dataManager.set(SYNC_DEGREES_PER_SECOND, degrees);
    }

    public State getState() {
        return State.values()[dataManager.get(SYNC_STATE)];
    }

    //use the ordinal for the state
    public void setState(State state) {
        dataManager.set(SYNC_STATE, state.ordinal());
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_MAX_SIZE, 1.5F);
        dataManager.register(SYNC_HEALTH, 3F);
        dataManager.register(SYNC_DEGREES_PER_SECOND, 5F);
        dataManager.register(SYNC_STATE, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (getBehaviour() != null && getBehaviour() instanceof WaterBubbleBehavior.Lobbed) {
            setVelocity(velocity().times(0.9));
        }
        if (getHealth() == 0) {
            this.setDead();
        }

        if (ticksExisted % 5 == 0) {
            BlockPos down = getPosition().down();
            IBlockState downState = world.getBlockState(down);
            if (downState.getBlock() == Blocks.FARMLAND) {
                int moisture = downState.getValue(BlockFarmland.MOISTURE);
                if (moisture < 7) world.setBlockState(down,
                        Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, moisture + 1));
            }
        }

        boolean inWaterSource = false;
        if (!world.isRemote && ticksExisted % 2 == 1 && ticksExisted > 10) {
            for (int x = 0; x <= 1; x++) {
                for (int z = 0; z <= 1; z++) {
                    BlockPos pos = new BlockPos(posX + x * width, posY, posZ + z * width);
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock() == Blocks.WATER && state.getValue(BlockLiquid.LEVEL) == 0) {
                        inWaterSource = true;
                        break;
                    }
                }
            }
        }
		/*
		if (!world.isRemote && inWaterSource) {
			setDead();
			if (getOwner() != null) {
				BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
				if (data != null) {
					data.removeStatusControl(StatusControlController.LOB_BUBBLE);
				}
			}
		}**/


//        //particles!
//        if (world.isRemote && getOwner() != null) {
//            //Colours: 0, 102, 255, 255 in order of r, g, b, a
//            //Particles are * 2 * PI because that's the circumference of a circle and idk.
//            //Use the bottom of the entity cause my method is bad and shifts the centre point up. Dw about it.
//            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(60, 102, 255, 160)
//                    .time(14).scale(0.5F).spawnEntity(this).element(BendingStyles.get(Waterbending.ID))
//                    .swirl((int) (getAvgSize() * 14), (int) (getAvgSize() * 6 * Math.PI),
//                            getAvgSize(), getAvgSize() * 5, getDegreesPerSecond() * getAvgSize(),
//                            -0.5F / getAvgSize(), this, world, false, AvatarEntityUtils.getBottomMiddleOfEntity(this),
//                            ParticleBuilder.SwirlMotionType.OUT, false, true);
//
//
//        }


    }

    @Nullable
    @Override
    public SoundEvent[] getSounds() {
        return new SoundEvent[]{
                SoundEvents.ENTITY_GENERIC_SPLASH
        };
    }

    @Override
    public void Explode() {
         super.Explode();
    }

    @Override
    public void setDead() {
        cleanup();
        super.setDead();
        if (!world.isRemote && this.isDead) {
            //Thread.dumpStack();
        }
    }

    public void cleanup () {
        if (getOwner() != null) {
            BendingData data = BendingData.getFromEntity(getOwner());
            if (data != null) {
                data.removeStatusControl(StatusControlController.SHIELD_BUBBLE);
                data.removeStatusControl(StatusControlController.LOB_BUBBLE);
                data.removeStatusControl(StatusControlController.RESET_SHIELD_BUBBLE);
                //Swirl
                //Throw
            }
        }
    }


    @Override
    public boolean shouldExplode() {
        return !(getBehaviour() instanceof WaterBubbleBehavior.PlayerControlled ||
                getBehaviour() instanceof WaterBubbleBehavior.Appear);
    }

    @Override
    public boolean shouldDissipate() {
        return false;
    }

    @Override
    public void spawnExplosionParticles(World world, Vec3d pos) {

    }

    @Override
    public void spawnDissipateParticles(World world, Vec3d pos) {

    }

    @Override
    public void spawnPiercingParticles(World world, Vec3d pos) {

    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if (entity instanceof AvatarEntity) {
            ((AvatarEntity) entity).onMajorWaterContact();
            if (((AvatarEntity) entity).getAbility() != null && ((AvatarEntity) entity).getOwner() != null
                    && getBehaviour() != null && getBehaviour() instanceof WaterBubbleBehavior.PlayerControlled) {
                float damage = AbilityData.get(((AvatarEntity) entity).getOwner(), ((AvatarEntity) entity).getAbility().getName()).getLevel();
                if (((AvatarEntity) entity).getElement().equals(Firebending.ID)) {
                    damage *= 0.5;
                }
                if (((AvatarEntity) entity).getElement().equals(Lightningbending.ID)) {
                    damage *= 2;
                }
                if (((AvatarEntity) entity).getElement().equals(Waterbending.ID)) {
                    damage *= 0.75;
                }
                ((AvatarEntity) entity).onCollideWithSolid();
                this.setHealth(getHealth() - damage);
            }
        }
        if (getBehaviour() instanceof WaterBubbleBehavior.PlayerControlled) {
            if (entity instanceof EntityArrow) {
                float damage = (float) ((EntityArrow) entity).getDamage();
                Vector vel = Vector.getVelocity(entity).times(-1);
                entity.addVelocity(vel.x(), 0, vel.z());
                setHealth(getHealth() - damage);
            }
        }
    }

    @Override
    public boolean onCollideWithSolid() {
        return super.onCollideWithSolid();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setSourceBlock(compound.getBoolean("SourceBlock"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("SourceBlock", sourceBlock);
    }

    public boolean isSourceBlock() {
        return sourceBlock;
    }

    public void setSourceBlock(boolean sourceBlock) {
        this.sourceBlock = sourceBlock;
    }

    @Override
    public EntityLivingBase getController() {
        return getBehaviour() instanceof WaterBubbleBehavior.PlayerControlled ? getOwner() : null;
    }

    @Override
    public UUID getElement() {
        return Waterbending.ID;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return super.shouldRenderInPass(pass);
    }

    public enum State {
        BUBBLE,
        SHIELD,
        RING
    }
}
