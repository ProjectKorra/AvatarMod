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
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.entity.EntityWall;
import com.crowsofwar.avatar.entity.EntityWallSegment;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.*;

public class AbilityWall extends Ability {

	public AbilityWall() {
		super(Earthbending.ID, "wall");
		requireRaytrace(6, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();

		if (bender.consumeChi(STATS_CONFIG.chiWall)) {

			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();
			EnumFacing cardinal = entity.getHorizontalFacing();
			BendingData data = ctx.getData();

			AbilityData abilityData = data.getAbilityData(this);
			// This "power" variable is the player's experience, but power rating can boost
			// power by up to 25 points
			float power = abilityData.getTotalXp() + (float) ctx.getPowerRating() / 100 * 25;

			int reach = Math.round(SKILLS_CONFIG.wallReach);

			int whMin, whMax;
			Random random = new Random();
			if (power >= 100) {
				whMin = 4;
				whMax = 6;
			} else if (power >= 75) {
				whMin = 3;
				whMax = 5;
			} else if (power >= 50) {
				whMin = 3;
				whMax = 4;
			} else if (power >= 25) {
				whMin = 2;
				whMax = 4;
			} else {
				whMin = 1;
				whMax = 3;
			}
			if (ctx.getLevel() >= 2) {
				whMax += 2;
			}

			abilityData.addXp(SKILLS_CONFIG.wallRaised);

			// The range should increase the higher the level is.
			reach = abilityData.getLevel() <= 0 ? reach : reach + abilityData.getLevel();

			boolean wallCreated = false;
			if (ctx.getDynamicPath() == AbilityTreePath.MAIN) {
				wallCreated = createLinearWall(ctx, world, reach, cardinal, entity, whMin, whMax, 5, random);
			} else if (ctx.getDynamicPath() == AbilityTreePath.SECOND) {
				BlockPos wallPos = entity.getPosition().down();
				Block wallBlock = world.getBlockState(wallPos).getBlock();
				boolean createWall = true;

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

				// Last safety check
				if (wallBlock != Blocks.AIR) {
					wallCreated = createSurroundingWalls(world, wallPos, wallBlock, entity, whMin, whMax, random);
				}
			} else if (ctx.getDynamicPath() == AbilityTreePath.FIRST) {
				wallCreated = createLinearWall(ctx, world, reach, cardinal, entity, whMin, whMax, 7, random);
			}

			if (wallCreated) {
				ctx.getData().addStatusControl(DROP_WALL);
				ctx.getData().addStatusControl(PLACE_WALL);
				if (ctx.isDynamicMasterLevel(AbilityTreePath.FIRST)) {
					ctx.getData().addStatusControl(SHOOT_WALL);
				} else if (ctx.isDynamicMasterLevel(AbilityTreePath.SECOND)) {
					ctx.getData().addStatusControl(PUSH_WALL);
				}
			}
		}
	}

	private boolean createLinearWall(AbilityContext ctx, World world, int reach, EnumFacing cardinal,
									 EntityLivingBase entity, int whMin, int whMax, int lenght, Random random) {
		// Used so that the wall can be more precisely placed if needed, useful when
		// used for building. However, during a fight, it will still spawn even if not
		// directly looking at the ground. However this won't override the maximum reach
		// distance.
		BlockPos lookPos;
		// Down 1 block so that we actually get a block...
		BlockPos entityPos = entity.getPosition().down();
		if (ctx.isLookingAtBlock()) {
			lookPos = ctx.getLookPosI().toBlockPos();
			if (lookPos.distanceSq(entityPos) > reach) {
				lookPos = entityPos.offset(cardinal, reach);
			}
		} else {
			lookPos = entityPos.offset(cardinal, reach);
		}

		Block lookBlock = world.getBlockState(lookPos).getBlock();
		if (lookBlock == Blocks.TALLGRASS) {
			lookPos = lookPos.down();
		} else if (lookBlock == Blocks.DOUBLE_PLANT) {
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

		// The offset is used to re-center the wall
		return createWall(world, lookPos.offset(cardinal.rotateY(), -1), lookBlock, cardinal, entity, whMin, whMax, 0,
				lenght, random);
	}

	/*
	 * Spawn 4 walls around the bender
	 */
	private boolean createSurroundingWalls(World world, BlockPos lookPos, Block lookBlock, EntityLivingBase entity,
										   int whMin, int whMax, Random random) {
		boolean wall0Created = false, wall1Created = false, wall2Created = false, wall3Created = false;

		wall0Created = createWall(world, lookPos.offset(EnumFacing.EAST, 3), lookBlock, EnumFacing.EAST, entity, whMin,
				whMax, 0, 5, random);
		wall1Created = createWall(world, lookPos.offset(EnumFacing.NORTH, 3), lookBlock, EnumFacing.NORTH, entity,
				whMin, whMax, 0, 5, random);
		wall2Created = createWall(world, lookPos.offset(EnumFacing.SOUTH, 3), lookBlock, EnumFacing.SOUTH, entity,
				whMin, whMax, 0, 5, random);
		wall3Created = createWall(world, lookPos.offset(EnumFacing.WEST, 3), lookBlock, EnumFacing.WEST, entity, whMin,
				whMax, 0, 5, random);

		return wall0Created || wall1Created || wall2Created || wall3Created;
	}

	/*
	 * Spawn a wall with provided settings
	 */
	private boolean createWall(World world, BlockPos wallPos, Block wallBlock, EnumFacing direction,
							   EntityLivingBase entity, int whMin, int whMax, int height, int width, Random random) {
		EntityWall wall = new EntityWall(world);
		if (STATS_CONFIG.bendableBlocks.contains(wallBlock) || STATS_CONFIG.plantBendableBlocks.contains(wallBlock)) {
			wall.setPosition(wallPos.getX() + .5, wallPos.getY(), wallPos.getZ() + .5);
			wall.setOwner(entity);
			for (int i = 0; i < width; i++) {

				int wallHeight = whMin + random.nextInt(whMax - whMin + 1);

				int horizMod = -2 + i;
				int x = wallPos.getX()
						+ (direction == EnumFacing.NORTH || direction == EnumFacing.SOUTH ? horizMod : 0);
				int y = wallPos.getY() - 4;
				int z = wallPos.getZ() + (direction == EnumFacing.EAST || direction == EnumFacing.WEST ? horizMod : 0);

				EntityWallSegment seg = new EntityWallSegment(world);
				seg.attachToWall(wall);
				seg.setPosition(x + .5, y, z + .5);
				seg.setDirection(direction);
				seg.setOwner(entity);
				seg.setAbility(this);

				boolean foundAir = false, dontBreakMore = false;
				for (int j = EntityWallSegment.SEGMENT_HEIGHT - 1; j >= 0; j--) {
					BlockPos pos = new BlockPos(x, y + j, z);
					IBlockState state = world.getBlockState(pos);
					boolean bendable = STATS_CONFIG.bendableBlocks.contains(state.getBlock());
					if (!bendable || dontBreakMore) {
						state = Blocks.AIR.getDefaultState();
						dontBreakMore = true;
					}

					if (!foundAir && state.getBlock() == Blocks.AIR) {
						seg.setSize(seg.width, 5 - j - 1);
						seg.setBlocksOffset(-(j + 1));
						seg.setPosition(seg.position().withY(y + j + 1));
						foundAir = true;
					}
					if (foundAir && state.getBlock() != Blocks.AIR) {
						// Extend bounding box
						seg.setSize(seg.width, 5 - j);
						seg.setBlocksOffset(-j);
						seg.setPosition(seg.position().withY(y + j));
					}

					seg.setBlock(j, state);
					if (bendable && !dontBreakMore)
						world.setBlockToAir(pos);

					if (j == 5 - wallHeight) {
						dontBreakMore = true;
					}

				}

				world.spawnEntity(seg);
			}
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
