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

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class EntityEarthspike extends EntityOffensive {

    public EntityEarthspike(World world) {
        super(world);
        setPositionNonDirty();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
    }

    @Override
    public boolean shouldExplode() {
        return false;
    }

    @Override
    public boolean shouldDissipate() {
        return true;
    }

    @Override
    public boolean isPiercing() {
        return true;
    }

    @Override
    public BendingStyle getElement() {
        return new Earthbending();
    }

    @Override
    public void spawnDissipateParticles(World world, Vec3d pos) {
        if (world.isRemote) {
            for (int i = 0; i < getLifeTime() * 0.5; i++)
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, pos.x, pos.y, pos.z, world.rand.nextGaussian() / 20,
                        world.rand.nextDouble() / 20, world.rand.nextGaussian() / 20, Block.getStateId(world.getBlockState(getPosition().down())));
        }
    }

    @Override
    public void spawnExplosionParticles(World world, Vec3d pos) {

    }

    @Override
    public void spawnPiercingParticles(World world, Vec3d pos) {

    }

    @Override
    public void Dissipate() {
        setEntitySize(getHeight() / 1.35F, getWidth() / 1.35F);
        if (getHeight() < 0.5) {
            spawnDissipateParticles(world, AvatarEntityUtils.getBottomMiddleOfEntity(this));
            setDead();
        }
        if (ticksExisted > getLifeTime() + 20) {
            setDead();
            spawnDissipateParticles(world, AvatarEntityUtils.getBottomMiddleOfEntity(this));
        }
    }

    @Nullable
    @Override
    public SoundEvent[] getSounds() {
        return new SoundEvent[0];
    }

    @Override
    public void onEntityUpdate() {
        // Add width and height stuff
        super.onEntityUpdate();

        this.motionX *= 0;
        this.motionY *= 0;
        this.motionZ *= 0;


        // Destroy non-solid blocks in the Earthspike
        IBlockState inBlock = world.getBlockState(getPosition());
        if (inBlock.getBlock() != Blocks.AIR && !inBlock.isFullBlock()) {
            if (inBlock.getBlockHardness(world, getPosition()) == 0) {
                breakBlock(getPosition());
            } else {
                setDead();
            }
        }

        if (!Earthbending.isBendable(world, getPosition(), world.getBlockState(getPosition().down()), 2))
            this.Dissipate();
    }

    @Override
    public void setDead() {
        super.setDead();
        if (this.isDead && !world.isRemote)
            Thread.dumpStack();
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public int getFireTime() {
        return 0;
    }

    @Override
    public void resetPositionToBB() {
        //Fixes janky positioning
    }
}
