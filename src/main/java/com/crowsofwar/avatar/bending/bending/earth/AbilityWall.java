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

package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.entity.EntityWall;
import com.crowsofwar.avatar.entity.EntityWallSegment;
import com.crowsofwar.avatar.entity.data.WallBehavior;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.*;

public class AbilityWall extends Ability {

    public static final String
            WALL_REACH = "wallReach",
            SIZE_MIN = "sizeMin",
            SIZE_MAX = "sizeMax",
            MULTI_WALL = "multiWall",
            SHOOT_BLOCKS = "shootBlocks",
            SHOOT_WALLS = "shootWall";

    public AbilityWall() {
        super(Earthbending.ID, "wall");
        requireRaytrace(20, false);
    }

    @Override
    public void init() {
        super.init();
        addProperties(WALL_REACH, SIZE_MIN, SIZE_MAX);
        addBooleanProperties(SHOOT_BLOCKS, MULTI_WALL, SHOOT_WALLS);
    }

    @Override
    public void execute(AbilityContext ctx) {

        Bender bender = ctx.getBender();

        if (bender.consumeChi(getChiCost(ctx))) {

            EntityLivingBase entity = ctx.getBenderEntity();
            World world = ctx.getWorld();
            EnumFacing cardinal = entity.getHorizontalFacing();
            BendingData data = ctx.getData();

            AbilityData abilityData = data.getAbilityData(this);

            int reach = Math.round(getProperty(WALL_REACH, ctx).floatValue());

            int whMin, whMax;
            whMin = getProperty(SIZE_MIN, ctx).intValue();
            whMax = getProperty(SIZE_MAX, ctx).intValue();

            whMin *= abilityData.getDamageMult() * abilityData.getXpModifier();
            whMax *= abilityData.getDamageMult() * abilityData.getXpModifier();
            reach *= abilityData.getDamageMult() * abilityData.getXpModifier();

            Random random = new Random();
            boolean wallCreated = false;

            if (getBooleanProperty(MULTI_WALL, ctx)) {
                BlockPos wallPos = entity.getPosition().down().add(entity.getLookVec().x, 0, entity.getLookVec().z);
                Block wallBlock = world.getBlockState(wallPos).getBlock();

                // Allow bending even if the block is lower than the bender by 1-2 (by default)
                // blocks
                if (wallBlock == Blocks.AIR) {
                    for (int i = 0; i <= reach; i++) {
                        wallPos = wallPos.down();
                        wallBlock = world.getBlockState(wallPos).getBlock();
                        if (wallBlock != Blocks.AIR)
                            break;
                    }
                }

                if (!Earthbending.isBendable(world, wallPos, world.getBlockState(wallPos), 2)) {
                    Vector tempPos = Earthbending.getClosestEarthbendableBlock(entity, ctx, WALL_REACH, this, 2);
                    if (tempPos != null)
                        wallPos = tempPos.toBlockPos();
                    wallBlock = world.getBlockState(wallPos).getBlock();
                }

                // Last safety check
                if (wallBlock != Blocks.AIR) {
                    wallCreated = createSurroundingWalls(ctx, world, wallPos, wallBlock, entity, whMin, whMax, random);

                }
            } else {
                wallCreated = createLinearWall(ctx, world, reach, cardinal, entity, whMin, whMax, whMax - 1, whMax, random);

            }

            if (wallCreated) {
                abilityData.addXp(getProperty(XP_USE).floatValue());
                ctx.getData().addStatusControl(DROP_WALL);
                ctx.getData().addStatusControl(PLACE_WALL);
                if (getBooleanProperty(SHOOT_BLOCKS, ctx)) {
                    ctx.getData().addStatusControl(SHOOT_WALL);
                }
                if (getBooleanProperty(SHOOT_WALLS, ctx)) {
                    ctx.getData().addStatusControl(PUSH_WALL);
                }
            }
        }
    }

    private boolean createLinearWall(AbilityContext ctx, World world, int reach, EnumFacing cardinal,
                                     EntityLivingBase entity, int whMin, int whMax, int height, int length, Random random) {
        // Used so that the wall can be more precisely placed if needed, useful when
        // used for building. However, during a fight, it will still spawn even if not
        // directly looking at the ground. However this won't override the maximum reach
        // distance.
        BlockPos lookPos;
        // Down 1 block so that we actually get a block...
        BlockPos entityPos = entity.getPosition().down();
        if (ctx.isLookingAtBlock() && ctx.getLookPosI() != null) {
            lookPos = ctx.getLookPosI().toBlockPos();
            if (lookPos.distanceSq(entityPos) > reach) {
                lookPos = entityPos.offset(cardinal, reach);
            }
        } else {
            lookPos = entityPos.offset(cardinal, reach);
        }

        Block lookBlock = world.getBlockState(lookPos).getBlock();
        if (lookBlock instanceof BlockTallGrass) {
            lookPos = lookPos.down();
        } else if (lookBlock instanceof BlockDoublePlant) {
            lookPos = lookPos.down(2);
        }

        // Allow bending even if the block is lower than the bender by 1-2 (by default)
        // blocks
        if (lookBlock == Blocks.AIR) {
            for (int i = 0; i <= reach; i++) {
                lookPos = lookPos.down();
                lookBlock = world.getBlockState(lookPos).getBlock();
                if (lookBlock != Blocks.AIR)
                    break;
            }
        }

        if (Earthbending.isBendable(world, lookPos, world.getBlockState(lookPos), 2))
            // The offset is used to re-center the wall
            return createWall(ctx, world, lookPos.offset(cardinal.rotateY(), -1), lookBlock, cardinal, entity, whMin, whMax, height,
                    length, random);
        else if (Earthbending.getClosestEarthbendableBlock(entity, ctx, WALL_REACH,this, 2) != null)
            lookPos = Objects.requireNonNull(Earthbending.getClosestEarthbendableBlock(entity, ctx, WALL_REACH, this, 2)).toBlockPos();

        return createWall(ctx, world, lookPos.offset(cardinal.rotateY(), -1), lookBlock, cardinal, entity, whMin, whMax, height,
                length, random);

    }

    /*
     * Spawn 4 walls around the bender
     */
    private boolean createSurroundingWalls(AbilityContext ctx, World world, BlockPos lookPos, Block lookBlock, EntityLivingBase entity,
                                           int whMin, int whMax, Random random) {
        boolean wall0Created, wall1Created, wall2Created, wall3Created;

        wall0Created = createWall(ctx, world, lookPos.offset(EnumFacing.EAST, 3), lookBlock, EnumFacing.EAST, entity, whMin,
                whMax, whMax - 1, whMax, random);
        wall1Created = createWall(ctx, world, lookPos.offset(EnumFacing.NORTH, 3), lookBlock, EnumFacing.NORTH, entity,
                whMin, whMax, whMax - 1, whMax, random);
        wall2Created = createWall(ctx, world, lookPos.offset(EnumFacing.SOUTH, 3), lookBlock, EnumFacing.SOUTH, entity,
                whMin, whMax, whMax - 1, whMax, random);
        wall3Created = createWall(ctx, world, lookPos.offset(EnumFacing.WEST, 3), lookBlock, EnumFacing.WEST, entity, whMin,
                whMax, whMax - 1, whMax, random);

        return wall0Created || wall1Created || wall2Created || wall3Created;
    }

    /*
     * Spawn a wall with provided settings
     */
    private boolean createWall(AbilityContext ctx, World world, BlockPos wallPos, Block wallBlock, EnumFacing direction,
                               EntityLivingBase entity, int whMin, int whMax, int height, int width, Random random) {
        EntityWall wall = new EntityWall(world);
        if (Earthbending.isBendable(world, wallPos, world.getBlockState(wallPos), 2) || STATS_CONFIG.plantBendableBlocks.contains(wallBlock)) {
            wall.setPosition(wallPos.getX() + .5, wallPos.getY(), wallPos.getZ() + .5);
            wall.setOwner(entity);
            wall.setTier(getCurrentTier(ctx));
            for (int i = 0; i < width; i++) {

                int wallHeight = AvatarUtils.getRandomNumberInRange(1, height) + (whMax - whMin);

                int horizMod = -(height / 2) + i;
                int x = wallPos.getX()
                        + (direction == EnumFacing.NORTH || direction == EnumFacing.SOUTH ? horizMod : 0);
                int y = wallPos.getY() - (height - 1);
                int z = wallPos.getZ() + (direction == EnumFacing.EAST || direction == EnumFacing.WEST ? horizMod : 0);

                EntityWallSegment seg = new EntityWallSegment(world);
                seg.attachToWall(wall);
                seg.setPosition(x + .5, y, z + .5);
                seg.setDirection(direction);
                seg.setOwner(entity);
                seg.setAbility(this);
                seg.setSegmentHeight(height);
                seg.setBehavior(new WallBehavior.Rising());
                seg.setTier(getCurrentTier(ctx));

                boolean foundAir = false, dontBreakMore = false;
                for (int j = seg.getSegmentHeight() - 1; j >= 0; j--) {
                    BlockPos pos = new BlockPos(x, y + j, z);
                    IBlockState state = world.getBlockState(pos);
                    boolean bendable = Earthbending.isBendable(world, pos, state, 2);
                    if (!bendable || dontBreakMore) {
                        state = Blocks.AIR.getDefaultState();
                        dontBreakMore = true;
                    }

                    if (!foundAir && state.getBlock() == Blocks.AIR) {
                        seg.setSize(seg.width, height - j - 1);
                        seg.setBlocksOffset(-(j + 1));
                        seg.setPosition(seg.position().withY(y + j + 1));
                        foundAir = true;
                    }
                    if (foundAir && state.getBlock() != Blocks.AIR) {
                        // Extend bounding box
                        seg.setSize(seg.width, height - j);
                        seg.setBlocksOffset(-j);
                        seg.setPosition(seg.position().withY(y + j));
                    }

                    seg.setBlock(j, state);
                    if (bendable && !dontBreakMore && !world.isRemote)
                        world.setBlockToAir(pos);

                    if (j <= height - wallHeight || AvatarUtils.getRandomNumberInRange(1, whMax) / 10F * height < random.nextDouble()) {
                        dontBreakMore = true;
                    }

                    if (seg.height > seg.getSegmentHeight())
                        seg.setSize(seg.width, seg.getSegmentHeight());

                }

                if (!world.isRemote)
                    world.spawnEntity(seg);
            }
            if (!world.isRemote)
                world.spawnEntity(wall);

            return true;
        }
        return false;
    }

    @Override
    public boolean isUtility() {
        return true;
    }

    @Override
    public int getBaseTier() {
        return 3;
    }
}
