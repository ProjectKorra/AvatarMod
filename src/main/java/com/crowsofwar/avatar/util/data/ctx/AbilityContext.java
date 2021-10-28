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
package com.crowsofwar.avatar.util.data.ctx;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.SourceInfo;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.util.Raytrace.Result;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class AbilityContext extends BendingContext {

    private final Ability ability;
    private final double powerRating;
    private final boolean switchPath;

    public AbilityContext(BendingData data, Result raytrace, Ability ability, EntityLivingBase entity,
                          double powerRating, boolean switchPath) {
        super(data, entity, raytrace);
        this.ability = ability;
        this.powerRating = powerRating;
        this.switchPath = switchPath;
    }

    public AbilityContext(BendingData data, EntityLivingBase entity, Bender bender, Result raytrace, Ability ability,
                          double powerRating, boolean switchPath) {
        super(data, entity, bender, raytrace);
        this.ability = ability;
        this.powerRating = powerRating;
        this.switchPath = switchPath;
    }

    public AbilityData getAbilityData() {
        return getData().getAbilityData(ability);
    }

    public int getLevel() {
        return getAbilityData().getLevel();
    }

    public AbilityTreePath getPath() {
        return getAbilityData().getPath();
    }

    /*
     * Same as getPath(), but accounts for a dynamic change
     */
    public AbilityTreePath getDynamicPath() {
        AbilityTreePath currentPath = getPath();
        if (switchPath) {
            if (currentPath == AbilityTreePath.FIRST) {
                return AbilityTreePath.SECOND;
            } else if (currentPath == AbilityTreePath.SECOND) {
                return AbilityTreePath.FIRST;
            } else {
                return AbilityTreePath.MAIN;
            }
        } else {
            return currentPath;
        }
    }

    /**
     * Returns true if ability is on level 4 and has selected that path.
     */
    public boolean isMasterLevel(AbilityTreePath path) {
        return getLevel() == 3 && getPath() == path;
    }

    /**
     * Same as isMasterLevel(), but accounts for a dynamic change
     */
    public boolean isDynamicMasterLevel(AbilityTreePath path) {
        return getLevel() == 3 && getDynamicPath() == path;
    }

    /**
     * Gets the current power rating, from -100 to +100.
     */
    public double getPowerRating() {
        return powerRating;
    }

    /**
     * Gets the power rating, but in the range 0.25 to 2.0 for convenience in damage
     * calculations.
     * <ul>
     * <li>-100 power rating gives 0.25; damage would be 1/4 of normal</li>
     * <li>0 power rating gives 1; damage would be the same as normal</li>
     * <li>100 power rating gives 2; damage would be twice as much as usual</li>
     */
    public double getPowerRatingDamageMod() {
        return getAbilityData().getDamageMult();
    }


    //Ok I actually need to redo this now. Need some way for abilities to hook into it.
    //Ok ok. I need to be able to pass a position.
    @Override
    public boolean consumeWater(int amount) {
        if (getAbilityData().getAbility() != null) {
            EntityLivingBase entity = getBenderEntity();
            World world = getWorld();
            Vector look = Vector.getLookRectangular(entity).times(0.25);
            Vector startPos = Vector.getEntityPos(entity);
            Vector firstPos = startPos.plus(look).minusY(1);
            Vector secondPos = firstPos.minusY(1);
            BlockPos pos1 = firstPos.toBlockPos();
            BlockPos pos2 = secondPos.toBlockPos();
            //Checks 2 different places to ensure there's a block that's found.
            boolean firstBendable;
            boolean secondBendable;

            //Either the wave can go on land or there's a compatible block to use
            firstBendable = Waterbending.isBendable(getAbilityData().getAbility(), world.getBlockState(pos1),
                    entity);
            secondBendable = Waterbending.isBendable(getAbilityData().getAbility(), world.getBlockState(pos2),
                    entity);
            Vector pos = Waterbending.getClosestWaterbendableBlock(getBenderEntity(), getAbilityData().getAbility(),
                    this);

            //Setting the source info allows the ability to access the source later
            if (isBendable(amount, world, pos1, firstBendable)) {
                getAbilityData().setSourceInfo(new SourceInfo(world.getBlockState(pos1), world,
                        pos1));
                return true;
            }
            if (isBendable(amount, world, pos2, secondBendable)) {
                getAbilityData().setSourceInfo(new SourceInfo(world.getBlockState(pos2), world,
                        pos2));
                return true;
            }//Consumes water
            if (pos != null) {
                return consumeWater(amount, pos.toBlockPos(), world.getBlockState(pos.toBlockPos()));
            }
        }
        return super.consumeWater(amount);
    }

    private boolean isBendable(int amount, World world, BlockPos pos1, boolean bendable) {
        if (bendable) {
            Block block = world.getBlockState(pos1).getBlock();
            if (STATS_CONFIG.plantBendableBlocks.contains(world.getBlockState(pos1).getBlock())) {
                if (amount > 0) {
                    world.setBlockToAir(pos1);
                }
                return true;
            }
            if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                if (amount > 2) {
                    world.setBlockToAir(pos1);
                }
                return true;
            }
            if (STATS_CONFIG.waterBendableBlocks.contains(world.getBlockState(pos1).getBlock())) {
                if (amount > 1) {
                    world.setBlockToAir(pos1);
                }
                return true;
            }
        }
        return false;
    }


    //Same as the other one but it actually lets you pass a blockstate and pos,
    //so there's less redundancy + positions being calculated.
    @Override
    public boolean consumeWater(int amount, BlockPos targetPos, IBlockState blockState) {
        if (isBendable(amount, getWorld(), targetPos, Waterbending.isBendable(getAbilityData().getAbility(), blockState,
                getBenderEntity())))
            return true;
        return super.consumeWater(amount, targetPos, blockState);
    }
}
