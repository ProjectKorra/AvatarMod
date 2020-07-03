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
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ScheduledDestroyBlock;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;
import static net.minecraft.init.Blocks.AIR;

/**
 * @author CrowsOfWar
 */
public class AbilityMining extends Ability {

	public AbilityMining() {
		super(Earthbending.ID, "mine_blocks");
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();
		float chi = ctx.isMasterLevel(FIRST) ? STATS_CONFIG.chiMiningMaster : STATS_CONFIG.chiMining;

		if (bender.consumeChi(chi) && !ctx.getWorld().isRemote) {

			EntityLivingBase entity = ctx.getBenderEntity();
			World world = ctx.getWorld();

			AbilityData abilityData = ctx.getAbilityData();
			abilityData.addXp(SKILLS_CONFIG.miningUse);

			Vector direction = getDirection(entity);
			List<Vector> rays = getRaysStartPos(entity, direction);

			int dist = getDistance(abilityData.getLevel(), abilityData.getPath());
			dist += (int) (ctx.getPowerRating() / 40);
			int fortune = getFortune(abilityData.getLevel(), abilityData.getPath());
			fortune += (int) Math.ceil(ctx.getPowerRating() / 50);
			int breakBlockTime = ctx.isMasterLevel(FIRST) ? 1 : 3;

			// For keeping track of already inspected/to-be-inspected positions
			// of ore blocks
			// orePos: Position that needs to be inspected
			// alreadyMinedOres: Position that was already inspected (since the
			// queue items will be deleted)
			Queue<BlockPos> oresToBeMined = new LinkedList<>();
			Set<BlockPos> alreadyMinedOres = new HashSet<>();

			for (Vector ray : rays) {

				for (int i = 1; i <= dist; i++) {

					BlockPos pos = ray.plus(direction.times(i)).toBlockPos();
					Block block = world.getBlockState(pos).getBlock();

					// Mark any ores that were found; doesn't actually mine them yet
					if (isBreakableOre(world, pos)) {
						oresToBeMined.add(pos);
						alreadyMinedOres.add(pos);
					}

					// Actually break the block here
					boolean success = breakBlock(pos, ctx, i * breakBlockTime, fortune);
					// Stop at non-breakable blocks
					if (!success && block != Blocks.AIR) {
						break;
					}
					if (success)
						abilityData.addXp(SKILLS_CONFIG.miningUse * ((float) 1 / dist));

				}

			}

			// Here is where ore floodfill mining is actually performed
			if (abilityData.getPath() == SECOND) {
				mineNextOre(ctx, oresToBeMined, alreadyMinedOres, 0);
			}

		}

	}

	/**
	 * Calculates a random distance to mine blocks based on the current level.
	 */
	private int getDistance(int level, AbilityTreePath path) {

		int min, max;
		if (level == 3 && path == FIRST) {

			min = 5;
			max = 7;

		} else if (level == 3) {

			min = 4;
			max = 6;

		} else if (level == 2) {

			min = 3;
			max = 5;

		} else if (level == 1) {

			min = 2;
			max = 4;

		} else {

			min = 2;
			max = 3;

		}

		return (int) (min + Math.random() * (max - min));

	}

	private int getFortune(int level, AbilityTreePath path) {
		if (level == 3) {
			return path == SECOND ? 3 : 2;
		} else if (level == 2) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the direction the entity is facing. This is not a unit vector, but instead each
	 * component is either -1, 0, or 1.
	 */
	private Vector getDirection(EntityLivingBase entity) {

		// Just return the look vector, but each component is rounded (to either -1, 0, or 1)

		Vector look = Vector.getLookRectangular(entity);
		return new Vector(Math.round(look.x()), Math.round(look.y()), Math.round(look.z()));

	}

	/**
	 * In the mining operation, the blocks are mined in multiple "rays", where one block gets
	 * mined first and then it continues in one direction. This method gets the starting position
	 * of each "ray", and then the same direction for every ray can be determined using
	 * {@link #getDirection(EntityLivingBase)}.
	 */
	private List<Vector> getRaysStartPos(EntityLivingBase entity, Vector direction) {

		// Each starting position of the ray to mine out
		List<Vector> rays = new ArrayList<>();
		rays.add(new Vector(entity.getPosition()));
		rays.add(new Vector(entity.getPosition().up()));

		// When yaw is diagonal (not along cardinal direction), add another ray of excavation
		// because the excavated blocks would only be diagonal and you wouldn't be able to walk
		// through them
		if (direction.x() != 0 && direction.z() != 0) {
			rays.add(new Vector(entity.getPosition().east()));
			rays.add(new Vector(entity.getPosition().east().up()));
		}
		// When excavating up/down (ie making a stairway), add height to so you don't bump
		// your head
		if (direction.y() != 0) {
			rays.add(new Vector(entity.getPosition().up(2)));
		}

		return rays;

	}

	/**
	 * Breaks the block at the specified position, but doesn't break
	 * non-bendable blocks. Returns false if not able to break (since the block
	 * isn't bendable).
	 */
	private boolean breakBlock(BlockPos pos, AbilityContext ctx, int delay, int fortune) {

		World world = ctx.getWorld();
		Block block = world.getBlockState(pos).getBlock();
		AvatarWorldData wd = AvatarWorldData.getDataFromWorld(world);

		boolean bendable = STATS_CONFIG.bendableBlocks.contains(block) && block != AIR;
		if (bendable) {

			boolean drop = !ctx.getBender().isCreativeMode();
			wd.getScheduledDestroyBlocks().add(new ScheduledDestroyBlock(wd, pos, delay, drop, fortune));

			return true;

		} else {
			return false;
		}

	}

	/**
	 * Represents a step in the flood-fill algorithm to destroy ore veins. Looks
	 * on the next flagged ore block and mines it, then inspects nearby blocks
	 * and flags any ores for mining. Finally, recursively calls mineNextOre again so the
	 * floodfill process continues.
	 *
	 * @param queue
	 * @param alreadyInspected
	 * @param ctx
	 * @param oresMined        How many ores have been mined so far. When calling this method from
	 *                         outside, should be 0. When the method recursively calls itself, this
	 *                         parameter increases. This allows the method to know when it has mined
	 *                         enough ores and should stop
	 */
	private void mineNextOre(AbilityContext ctx, Queue<BlockPos> queue, Set<BlockPos>
			alreadyInspected, int oresMined) {

		World world = ctx.getWorld();
		BlockPos pos = queue.poll();

		if (pos == null) return;
		if (breakBlock(pos, ctx, oresMined * 3 + 20, 3)) {
			oresMined++;
			ctx.getAbilityData().addXp(SKILLS_CONFIG.miningBreakOre);
		}

		// Search nearby blocks, and flag ores for mining

		for (int j = 0; j < 6; j++) {

			EnumFacing facing = EnumFacing.values()[j];
			BlockPos inspectingPos = pos.offset(facing);
			Block inspectingBlock = world.getBlockState(inspectingPos).getBlock();

			if (isBreakableOre(world, inspectingPos) && !alreadyInspected.contains(inspectingPos)) {

				queue.add(inspectingPos);
				alreadyInspected.add(inspectingPos);

			}

		}

		// Inspect the next ore
		if (!queue.isEmpty() && oresMined < 15) {
			mineNextOre(ctx, queue, alreadyInspected, oresMined);
		}

	}

	private boolean isBreakableOre(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof BlockOre || block instanceof BlockRedstoneOre;
	}


}
