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

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

public class EntityWave extends EntityOffensive {

    private static final DataParameter<Boolean> SYNC_RUN_ON_LAND = EntityDataManager.createKey(EntityWave.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SYNC_RIDEABLE = EntityDataManager.createKey(EntityWave.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SYNC_GROWS = EntityDataManager.createKey(EntityWave.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SYNC_PULLS = EntityDataManager.createKey(EntityWave.class,
            DataSerializers.BOOLEAN);

    private Vector initialPosition;
    private double maxTravelDistanceSq;

    /**
     * @param world The world the entity spawns in
     */
    public EntityWave(World world) {
        super(world);
        setSize(0.125F, 0.125F);
        this.noClip = true;
        this.ignoreFrustumCheck = true;
    }


    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_RUN_ON_LAND, false);
        dataManager.register(SYNC_RIDEABLE, false);
        dataManager.register(SYNC_GROWS, false);
        dataManager.register(SYNC_PULLS, false);
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public double getExpandedHitboxWidth() {
        return 0.5;
    }

    @Override
    public double getExpandedHitboxHeight() {
        return 0.5;
    }


    public void setDistance(double dist) {
        maxTravelDistanceSq = dist * dist;
    }


    public double getSqrDistanceTravelled() {
        return position().sqrDist(initialPosition);
    }

    public void setRunOnLand(boolean land) {
        dataManager.set(SYNC_RUN_ON_LAND, land);
    }

    public boolean shouldRunOnLand() {
        return dataManager.get(SYNC_RUN_ON_LAND);
    }

    //TODO: Implement this
    public boolean isRideable() {
        return dataManager.get(SYNC_RIDEABLE);
    }

    public void setRideable(boolean rideable) {
        dataManager.set(SYNC_RIDEABLE, rideable);
    }

    public void setGrows(boolean grows) {
        dataManager.set(SYNC_GROWS, grows);
    }

    public boolean doesGrow() {
        return dataManager.get(SYNC_GROWS);
    }

    public void setPulls(boolean pulls) {
        dataManager.set(SYNC_PULLS, pulls);
    }

    public boolean doesPull() {
        return dataManager.get(SYNC_PULLS);
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public Vec3d getKnockback() {
        return super.getKnockback();
    }

    @Override
    public Vec3d getKnockbackMult() {
        return new Vec3d(STATS_CONFIG.ravineSettings.push, STATS_CONFIG.ravineSettings.push * 2,
                STATS_CONFIG.ravineSettings.push);
    }


    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        if (initialPosition == null) {
            initialPosition = position();
        }


        if (!world.isRemote && getSqrDistanceTravelled() > maxTravelDistanceSq) {
            Dissipate();
        }

        if (ticksExisted >= getLifeTime())
            setDead();

        BlockPos below = getPosition().offset(EnumFacing.DOWN);

        //Lowers the wave if there's a step below; also need to check against another boolean
        boolean bendableBlock = Waterbending.isBendable(Objects.requireNonNull(Abilities.get("wave")), world.getBlockState(below),
                getOwner());
        bendableBlock |= shouldRunOnLand() && world.getBlockState(below).isFullBlock();
        if (!bendableBlock && shouldDissipate()) {

            bendableBlock = Waterbending.isBendable(Objects.requireNonNull(Abilities.get("wave")),
                    world.getBlockState(below.down()), getOwner()) ||
                    shouldRunOnLand() && world.getBlockState(below.down()).isFullBlock();

            if (!bendableBlock)
                Dissipate();
            else {
                setPosition(position().minusY(1));
            }
        }


        boolean bendable = Waterbending.isBendable(Objects.requireNonNull(Abilities.get("wave")),
                world.getBlockState(getPosition()), getOwner()) || shouldRunOnLand() &&
                world.getBlockState(getPosition()).isFullBlock() && world.getBlockState(getPosition()).getBlock()
                != Blocks.AIR;

        if (bendable)
            setPosition(position().plusY(1));

        // Destroy non-solid blocks in the wave
        if (isDefaultBreakableBlock(world, getPosition())) {
            breakBlock(getPosition());
        }
    }

    @Nullable
    @Override
    public SoundEvent[] getSounds() {
        return new SoundEvent[0];
    }

    @Override
    public boolean isPiercing() {
        return true;
    }

    @Override
    public boolean shouldDissipate() {
        //TODO: Update this based on running on land + below block
        return false;
    }

    @Override
    public boolean shouldExplode() {
        //TODO: Update this based on running on land + below block
        return false;
    }

    @Override
    public int getFireTime() {
        return 0;
    }

    @Override
    public void Dissipate() {
        if (getOwner() != null) {
            if (onCollideWithSolid() || !Waterbending.isBendable(Objects.requireNonNull(Abilities.get("wave")),
                    world.getBlockState(getPosition().down()), getOwner())) {
                spawnDissipateParticles(world, getPositionVector());
                setDead();
            }
        }
        super.Dissipate();
    }

    @Override
    public void setDead() {
        super.setDead();
        //This method is here in case I need to debug.
    }

    @Override
    public void resetPositionToBB() {
        //super.resetPositionToBB();
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
    public boolean onCollideWithSolid() {
        // Destroy if in a block
        IBlockState inBlock = world.getBlockState(getPosition());
        if (getOwner() != null) {
            if (inBlock.isFullBlock() && Waterbending.isBendable(Objects.requireNonNull(Abilities.get("wave")), world.getBlockState(getPosition()),
                    getOwner())) {
                inBlock = world.getBlockState(getPosition().up());
                if ((inBlock.getBlock() == Blocks.AIR || isDefaultBreakableBlock(world, getPosition().up()) &&
                        Waterbending.isBendable(Objects.requireNonNull(Abilities.get("wave")), world.getBlockState(getPosition()),
                                getOwner()))) {
                    setPosition(position().plusY(1));
                    return false;
                }

            }
            return true;
        }
        return false;
    }

    @Override
    public UUID getElement() {
        return Waterbending.ID;
    }

    public boolean isDefaultBreakableBlock(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return (state.getBlock() instanceof BlockSnow || state.getBlock()
                instanceof BlockBush) || state.getBlockHardness(world, pos) == 0 && !state.isFullBlock();
    }


}
