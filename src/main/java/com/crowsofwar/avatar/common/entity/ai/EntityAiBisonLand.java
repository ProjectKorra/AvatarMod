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
package com.crowsofwar.avatar.common.entity.ai;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.util.math.MathHelper.floor;

/**
 * Bison lands when he is hungry. This allows the bison to eat grass and to
 * consume less food points. Considered a MOVEMENT task, so has mutex bits 1.
 *
 * @author CrowsOfWar
 */
public class EntityAiBisonLand extends EntityAIBase {

	private final EntitySkyBison bison;

	/**
	 * The "timestamp" of the last time the bison attempted to land. This is really the value of bison.ticksExisted the
	 * last time a landing was attempted. Used to prevent bison from trying to land too often, which will hog all other
	 * tasks and cause bison to become "frozen".
	 */
	private int lastLandAttempt;

	public EntityAiBisonLand(EntitySkyBison bison) {
		this.bison = bison;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return bison.wantsGrass() && bison.ticksExisted - lastLandAttempt > 100;
	}

	@Override
	public void startExecuting() {

		lastLandAttempt = bison.ticksExisted;

		World world = bison.world;

		int tries = 0;
		Vector landing;
		boolean isValidPosition;
		do {

			landing = findLandingPoint().plusY(1);
			tries++;

			Block block = world.getBlockState(landing.toBlockPos().down()).getBlock();
			isValidPosition = (block == Blocks.GRASS || block == Blocks.TALLGRASS) && canFit(landing)
					&& canGetTo(landing);

		} while (!isValidPosition && tries < 5);

		if (isValidPosition) {

			landing = landing.plusY(1);
			bison.getMoveHelper().setMoveTo(landing.x(), landing.y() - 1, landing.z(), 1);

		}

	}

	@Override
	public boolean shouldContinueExecuting() {
		// Once got close to grass, close enough
		EntityMoveHelper mh = bison.getMoveHelper();
		if (bison.getDistanceSq(mh.getX(), mh.getY(), mh.getZ()) <= 5) {
			bison.getMoveHelper().action = Action.WAIT;
		}

		// Don't wander off until we have food!
		return !bison.isFull() && bison.isEatingGrass();
	}

	private Vector findLandingPoint() {

		double maxDist = 2;

		double x = bison.posX + (bison.getRNG().nextDouble() * 2 - 1) * maxDist;
		double z = bison.posZ + (bison.getRNG().nextDouble() * 2 - 1) * maxDist;

		int y = (int) bison.posY;
		while (!isSolidBlock(new BlockPos(x, y, z)) && y >= 0) {
			y--;
		}
		return new Vector(x, y, z);

	}

	private boolean canFit(Vector pos) {

		double minX = pos.x() - bison.width / 2;
		double maxX = pos.x() + bison.width / 2;
		double minY = pos.y();
		double maxY = pos.y() + bison.height;
		double minZ = pos.z() - bison.width / 2;
		double maxZ = pos.z() + bison.width / 2;

		for (int x = floor(minX); x <= maxX; x++) {
			for (int y = floor(minY); y <= maxY; y++) {
				for (int z = floor(minZ); z <= maxZ; z++) {
					if (isSolidBlock(new BlockPos(x, y, z))) {
						return false;
					}
				}
			}
		}

		return true;

	}

	/**
	 * Figure out whether the bison can get to that position by raytracing
	 */
	private boolean canGetTo(Vector target) {
		Vector current = Vector.getEntityPos(bison);
		Vector direction = target.minus(current).normalize();
		double range = current.dist(target);
		Raytrace.Result raytrace = Raytrace.raytrace(bison.world, current, direction, range + 1, false);
		Vector hitPos = raytrace.getPosPrecise() == null ? null : raytrace.getPosPrecise().plusY(1);
		return hitPos == null || hitPos.sqrDist(target) <= 2;
	}

	private boolean isSolidBlock(BlockPos pos) {
		World world = bison.world;
		return world.isBlockNormalCube(pos, false);
	}

}
