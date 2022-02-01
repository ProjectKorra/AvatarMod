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

import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.bending.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
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
public class EntityWaterBubble extends EntityArc<EntityWaterBubble.WaterControlPoint> implements IShieldEntity {

    //Used for determining the distance for the swirl
    public static final DataParameter<Float> SYNC_STREAM_RADIUS = EntityDataManager.createKey(EntityWaterBubble.class,
            DataSerializers.FLOAT);
    //For the shield/default bubble
    public static final DataParameter<Float> SYNC_DISTANCE = EntityDataManager.createKey(EntityWaterBubble.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_HEALTH = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_DEGREES_PER_SECOND = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_MAX_SIZE = EntityDataManager.createKey(EntityWaterBubble.class, DataSerializers.FLOAT);
    //Using ordinals of the STATE to sync them
    private static final DataParameter<Integer> SYNC_STATE = EntityDataManager.createKey(EntityWaterBubble.class,
            DataSerializers.VARINT);
    //GOTTA SYNC THE DEFAULT STATE TOO CAUSE WATER ARC
    private static final DataParameter<Integer> SYNC_DEFAULT_STATE = EntityDataManager.createKey(EntityWaterBubble.class,
            DataSerializers.VARINT);
    private static final DataParameter<Integer> SYNC_WATER_HITS = EntityDataManager.createKey(EntityWaterBubble.class,
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
        this.noClip = false;
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

    @Override
    public float getDamage() {
        if (isPiercing())
            return super.getDamage() / 2;
        return super.getDamage();
    }

    @Override
    public void setDamage(float damage) {
        //TODO: implement percentage health into damage (health / max health)
        super.setDamage(damage);
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

    public State getDefaultState() {
        return State.values()[dataManager.get(SYNC_DEFAULT_STATE)];
    }

    public void setDefaultState(State state) {
        dataManager.set(SYNC_DEFAULT_STATE, state.ordinal());
    }

    public float getSwirlRadius() {
        return dataManager.get(SYNC_STREAM_RADIUS);
    }

    //Used for determining the distance of the shield *and* the distance of the swirl.
    public void setSwirlRadius(float radius) {
        dataManager.set(SYNC_STREAM_RADIUS, radius);
    }

    public float getDistance() {
        return dataManager.get(SYNC_DISTANCE);
    }

    public void setDistance(float dist) {
        dataManager.set(SYNC_DISTANCE, dist);
    }

    public int getHitsLeft() {
        return dataManager.get(SYNC_WATER_HITS);
    }

    public void setHits(int hits) {
        dataManager.set(SYNC_WATER_HITS, hits);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_MAX_SIZE, 1.5F);
        dataManager.register(SYNC_HEALTH, 3F);
        dataManager.register(SYNC_DEGREES_PER_SECOND, 5F);
        dataManager.register(SYNC_STREAM_RADIUS, 2.5F);
        dataManager.register(SYNC_DISTANCE, 2F);
        dataManager.register(SYNC_STATE, State.BUBBLE.ordinal());
        dataManager.register(SYNC_DEFAULT_STATE, State.BUBBLE.ordinal());
        dataManager.register(SYNC_WATER_HITS, 0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (getBehaviour() != null && getBehaviour() instanceof WaterBubbleBehavior.Lobbed) {
            setVelocity(velocity().times(0.975));
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

    public void cleanup() {
        if (getOwner() != null) {
            BendingData data = BendingData.getFromEntity(getOwner());
            if (data != null) {
                //Base move
                data.removeStatusControl(StatusControlController.SHIELD_BUBBLE);
                data.removeStatusControl(StatusControlController.LOB_BUBBLE);
                data.removeStatusControl(StatusControlController.RESET_SHIELD_BUBBLE);
                data.removeStatusControl(StatusControlController.SWIRL_BUBBLE);
                data.removeStatusControl(StatusControlController.RESET_SWIRL_BUBBLE);
                //Added from wave
                data.removeStatusControl(StatusControlController.PUSH_SWIRL_BUBBLE);
                data.removeStatusControl(StatusControlController.RESET_SWIRL_BUBBLE);
                data.removeStatusControl(StatusControlController.PUSH_SHIELD_BUBBLE);
                data.removeStatusControl(StatusControlController.RESET_SHIELD_BUBBLE);
                //Added from water arc
                data.removeStatusControl(StatusControlController.MODIFY_WATER);

            }
        }
    }

    @Override
    public float getAoeDamage() {
        return super.getAoeDamage() / 2;
    }

    @Override
    public boolean shouldExplode() {
        //Add "Thrown" later
        return getBehaviour() instanceof WaterBubbleBehavior.Lobbed;
    }


    @Override
    public int getFireTime() {
        return 0;
    }

    @Override
    protected void updateCpBehavior() {
        //Copied from the super class & adjusted:
        if (getHitsLeft() <= 0)
            super.updateCpBehavior();
        else if (getOwner() != null) {
            getLeader().setPosition(position().plusY(height / 2));
            getLeader().setVelocity(velocity());

            // Move control points to follow leader

            //Iterates for every point up to the last
            for (int i = 1; i < points.size() - 1; i++) {

                ControlPoint leader = points.get(i - 1);
                ControlPoint p = points.get(i);
                Vector leadPos = leader.position();
                double sqrDist = p.position().sqrDist(leadPos);

                if (sqrDist > getControlPointTeleportDistanceSq() && getControlPointTeleportDistanceSq() != -1) {

                    Vector toFollowerDir = p.position().minus(leader.position()).normalize();

                    double idealDist = Math.sqrt(getControlPointTeleportDistanceSq());
                    if (idealDist > 1) idealDist -= 1; // Make sure there is some room

                    Vector revisedOffset = leader.position().plus(toFollowerDir.times(idealDist));
                    p.setPosition(revisedOffset);
                    leader.setPosition(revisedOffset);
                    p.setVelocity(Vector.ZERO);

                } else if (sqrDist > getControlPointMaxDistanceSq() && getControlPointMaxDistanceSq() != -1) {

                    Vector diff = leader.position().minus(p.position());
                    diff = diff.normalize().times(getVelocityMultiplier());
                    p.setVelocity(p.velocity().plus(diff));

                }
            }

            Vec3d pos = Vector.getEntityPos(getOwner()).toMinecraft().add(0, getOwner().getEyeHeight() / 2, 0);
            Vec3d look = getOwner().getLookVec();
            ControlPoint point = getControlPoint(getAmountOfControlPoints() - 1);
            point.setPosition(Vector.fromVec3d(pos.add(look.scale(2))));
        }
    }

    @Override
    public boolean shouldDissipate() {
        return false;
    }

    @Override
    public void spawnExplosionParticles(World world, Vec3d pos) {
        if (world.isRemote) {
            ParticleBuilder.create(ParticleBuilder.Type.CUBE).clr(255, 255, 255, 80).gravity(true)
                    .time(20).scale(1).spawnEntity(this).element(BendingStyles.get(Waterbending.ID)).collide(true)
                    .swirl((int) (getAvgSize() * 6), (int) (getAvgSize() * 6 * Math.PI * Math.max(getDegreesPerSecond() / 4, 1)),
                            getAvgSize(), getAvgSize() * 3, getDegreesPerSecond()
                                    * getAvgSize() * 2,
                            getDegreesPerSecond() / 2 + 2, this, world, true, AvatarEntityUtils.getMiddleOfEntity(this),
                            ParticleBuilder.SwirlMotionType.OUT, false, true);
        }
    }

    @Override
    public void spawnDissipateParticles(World world, Vec3d pos) {

    }

    @Override
    public void spawnPiercingParticles(World world, Vec3d pos) {

    }

    @Override
    public boolean isPiercing() {
        return super.isPiercing() || getHitsLeft() > 0;
    }


    @Override
    public void onCollideWithEntity(Entity entity) {

        if (isPiercing() || shouldExplode())
            super.onCollideWithEntity(entity);

        if (getHitsLeft() > 0) {
            setHits(getHitsLeft() - 1);
            setBehaviour(new WaterBubbleBehavior.PlayerControlled());
        }

        if (entity instanceof AvatarEntity) {
            ((AvatarEntity) entity).onMajorWaterContact();
            //Assume if the entity is a shield it's being player controlled. Probably. Hopefully.
            if (((AvatarEntity) entity).getAbility() != null && ((AvatarEntity) entity).getOwner() != null
                    && getBehaviour() != null && getState() == State.SHIELD) {
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
        if (super.onCollideWithSolid() && !(getBehaviour() instanceof WaterBubbleBehavior.PlayerControlled) && getHitsLeft() > 0) {
            setBehaviour(new WaterBubbleBehavior.PlayerControlled());
        }
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

    @Override
    protected double getControlPointTeleportDistanceSq() {
        return getControlPointMaxDistanceSq() * getAmountOfControlPoints();
    }

    @Override
    protected double getControlPointMaxDistanceSq() {
        return 0.5F * getMaxSize();
    }

    @Override
    public int getAmountOfControlPoints() {
        return 7;
    }

    @Override
    protected WaterControlPoint createControlPoint(float size, int index) {
        return new WaterControlPoint(this, size, posX, posY, posZ);
    }

    public enum State {
        //Default
        BUBBLE,
        //Shield
        SHIELD,
        //Stream around the player
        STREAM,
        //Water Arc
        ARC
    }

    public static class WaterControlPoint extends ControlPoint {

        public WaterControlPoint(EntityArc arc, float size, double x, double y, double z) {
            super(arc, size, x, y, z);
        }
    }
}
