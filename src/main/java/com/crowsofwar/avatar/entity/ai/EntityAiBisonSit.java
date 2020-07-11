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
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author CrowsOfWar
 */
public class EntityAiBisonSit extends EntityAIBase {

	private final EntitySkyBison bison;

	public EntityAiBisonSit(EntitySkyBison bison) {
		this.bison = bison;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return bison.isSitting();
	}

	@Override
	public void startExecuting() {
		World world = bison.world;
		Vector bisonPos = Vector.getEntityPos(bison);

		int y;
		for (y = (int) bisonPos.y(); y > 0; y--) {
			BlockPos pos = new BlockPos(bisonPos.x(), y, bisonPos.z());
			if (world.isSideSolid(pos, EnumFacing.UP)) {
				break;
			}
		}

		Random random = bison.getRNG();
		Vector randomized = new Vector((random.nextDouble() * 2 - 1) * 2, 0,
				(random.nextDouble() * 2 - 1) * 2);

		Vector targetPos = bisonPos.withY(y - 1).plus(randomized);
		bison.getMoveHelper().setMoveTo(targetPos.x(), targetPos.y(), targetPos.z(), 1);

	}

}
