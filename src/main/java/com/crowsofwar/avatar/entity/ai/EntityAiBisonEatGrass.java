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
package com.crowsofwar.avatar.entity.ai;

import com.crowsofwar.avatar.entity.mob.EntitySkyBison;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Bison eats grass when on ground. Designed to work with BisonLand AI so
 * BisonLand causes bison to land when hungry, then this makes the bison eat
 * grass. Since it is an action(non-movement) task the mutex bits are 0.
 *
 * @author CrowsOfWar
 */
public class EntityAiBisonEatGrass extends EntityAIBase {

	private final EntitySkyBison bison;

	/**
	 * When not eating grass, is -1. Then increments every tick that the bison
	 * has been eating grass.
	 */
	private int eatGrassTime;

	public EntityAiBisonEatGrass(EntitySkyBison bison) {
		this.bison = bison;
		eatGrassTime = -1;

		setMutexBits(0);
	}

	public boolean isEatingGrass() {
		return eatGrassTime > -1;
	}

	public int getEatGrassTime() {
		return eatGrassTime;
	}

	@Override
	public boolean shouldExecute() {
		Block standingOn = bison.world.getBlockState(bison.getPosition().down()).getBlock();
		return bison.wantsGrass() && isOnGround() && (!bison.isSitting() || standingOn == Blocks.GRASS);
	}

	@Override
	public void startExecuting() {
		shouldContinueExecuting();
	}

	@Override
	public boolean shouldContinueExecuting() {

		boolean keepExecuting = !bison.isFull() && isOnGround();
		World world = bison.world;
		EntityMoveHelper mh = bison.getMoveHelper();

		if (!isEatingGrass()) {
			// Just reached ground
			eatGrassTime = 0;
		}
		tryEatGrass();
		if (!bison.isSitting()) {
			bison.travel(0, 5, 0);
		}
		addRotations(0, 4);

		if (eatGrassTime > 80) {
			keepExecuting = false;
		}
		if (!keepExecuting) {
			eatGrassTime = -1;
		}

		return keepExecuting;

	}

	private void tryEatGrass() {
		eatGrassTime++;
		if (eatGrassTime % 30 == 29) {

			BlockPos downPos = bison.getPosition().down();
			World world = bison.world;

			boolean mobGriefing = world.getGameRules().getBoolean("mobGriefing");

			BlockPos ediblePos = null;

			Block block = world.getBlockState(downPos).getBlock();
			if (block == Blocks.GRASS) {
				ediblePos = downPos;
			} else {
				block = world.getBlockState(downPos.up()).getBlock();
				if (block == Blocks.TALLGRASS || block == Blocks.YELLOW_FLOWER
						|| block == Blocks.RED_FLOWER) {

					ediblePos = downPos.up();

				}
			}

			if (ediblePos != null) {

				if (mobGriefing) {
					world.playEvent(2001, ediblePos, Block.getIdFromBlock(Blocks.GRASS));
					if (block == Blocks.GRASS) {
						world.setBlockState(ediblePos, Blocks.DIRT.getDefaultState(), 2);
					}
				}

				bison.eatGrassBonus();

			} else {
				// Can't find food here
				addRotations(100, 160);
			}

			if (bison.isSitting()) {
				// Stop eating grass
				eatGrassTime = 81;
			}

		}
	}

	private boolean isSolidBlock(BlockPos pos) {
		World world = bison.world;
		return world.isBlockNormalCube(pos, false);
	}

	private boolean isOnGround() {
		BlockPos downPos = bison.getPosition().down();
		return isSolidBlock(downPos);
	}

	/**
	 * Rotate yaw by a random rotation. The supplied parameter determines the
	 * maximum rotation.
	 */
	private void addRotations(float min, float max) {
		int sign = bison.getRNG().nextBoolean() ? 1 : -1;
		bison.rotationYaw += sign * (min + bison.getRNG().nextFloat() * (max - min));
		bison.rotationYaw %= 360;
	}

}
