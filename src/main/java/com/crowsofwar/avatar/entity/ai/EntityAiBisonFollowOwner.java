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

import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.entity.mob.EntitySkyBison;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

import static com.crowsofwar.gorecore.util.Vector.getEyePos;

/**
 * @author CrowsOfWar
 */
public class EntityAiBisonFollowOwner extends EntityAIBase {

	private final EntitySkyBison bison;

	public EntityAiBisonFollowOwner(EntitySkyBison bison) {
		this.bison = bison;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {

		if (bison.isSitting()) return false;

		EntityPlayer owner = bison.getOwner();
		if (owner != null) {

			if (bison.getLeashHolder() == owner) {
				return true;
			}

			boolean followMode = BendingData.get(owner).getMiscData().getBisonFollowMode();
			if (followMode) {
				double maxDist = bison.getAttackTarget() == null ? 6 : 20;
				double maxDistSq = maxDist * maxDist;
				double distSq = bison.getDistanceSq(owner);
				return distSq >= maxDistSq && !bison.isSitting();
			}

		}

		return false;

	}

	@Override
	public boolean shouldContinueExecuting() {
		return false;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {

		EntityPlayer owner = bison.getOwner();
		if (owner == null) return;

		double dist = bison.getDistance(owner);

		Vector direction = getEyePos(owner).minus(getEyePos(bison)).normalize();
		Vector targetPos = getEyePos(bison).plus(direction.times(dist * 0.8));

		bison.getMoveHelper().setMoveTo(targetPos.x(), targetPos.y(), targetPos.z(), 1.0D);

	}

}
