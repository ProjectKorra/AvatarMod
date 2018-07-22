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

package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityWall;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

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
			// This "power" variable is the player's experience, but power rating can boost power by up to 25 points
			float power = abilityData.getTotalXp() + (float) ctx.getPowerRating() / 100 * 25;

			int whMin, whMax;
			Random random = new Random();
			if (power == 100) {
				whMin = whMax = 5;
			} else if (power >= 75) {
				whMin = 4;
				whMax = 5;
			} else if (power >= 50) {
				whMin = 3;
				whMax = 4;
			} else if (power >= 25) {
				whMin = 2;
				whMax = 4;
			} else {
				whMin = 2;
				whMax = 3;
			}

			abilityData.addXp(SKILLS_CONFIG.wallRaised);

			if (!ctx.isLookingAtBlock()) return;
			BlockPos lookPos = ctx.getLookPosI().toBlockPos();
			EntityWall wall = new EntityWall(world);

			Block lookBlock = world.getBlockState(lookPos).getBlock();
			if (lookBlock == Blocks.TALLGRASS) {
				lookPos = lookPos.down();
			} else if (lookBlock == Blocks.DOUBLE_PLANT) {
				lookPos = lookPos.down(2);
			}

			wall.setPosition(lookPos.getX() + .5, lookPos.getY(), lookPos.getZ() + .5);
			wall.setOwner(entity);
			for (int i = 0; i < 5; i++) {

				int wallHeight = whMin + random.nextInt(whMax - whMin + 1);

				int horizMod = -2 + i;
				int x = lookPos.getX()
						+ (cardinal == EnumFacing.NORTH || cardinal == EnumFacing.SOUTH ? horizMod : 0);
				int y = lookPos.getY() - 4;
				int z = lookPos.getZ()
						+ (cardinal == EnumFacing.EAST || cardinal == EnumFacing.WEST ? horizMod : 0);

				EntityWallSegment seg = new EntityWallSegment(world);
				seg.attachToWall(wall);
				seg.setPosition(x + .5, y, z + .5);
				seg.setDirection(cardinal);
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
					if (bendable && !dontBreakMore) world.setBlockToAir(pos);

					if (j == 5 - wallHeight) {
						dontBreakMore = true;
					}

				}

				world.spawnEntity(seg);
			}
			world.spawnEntity(wall);

			ctx.getData().addStatusControl(StatusControl.DROP_WALL);

		}

	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();

		int coolDown = 180;

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}

		if (ctx.getLevel() == 1) {
			coolDown = 160;
		}
		if (ctx.getLevel() == 2) {
			coolDown = 140;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 120;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 115;
		}
		return coolDown;
	}

}
