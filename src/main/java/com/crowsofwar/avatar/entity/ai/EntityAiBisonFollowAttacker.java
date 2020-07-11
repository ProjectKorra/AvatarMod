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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

/**
 * @author CrowsOfWar
 */
public class EntityAiBisonFollowAttacker extends EntityAIBase {

	private final EntitySkyBison bison;
	private final double followRange;

	public EntityAiBisonFollowAttacker(EntitySkyBison bison) {
		this.bison = bison;
		this.followRange = 60;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return bison.getAttackTarget() != null;
	}

	@Override
	public boolean shouldContinueExecuting() {

		EntityLivingBase target = bison.getAttackTarget();
		if (target == null || target.isDead) {
			return false;
		}

		if (bison.getDistanceSq(target) > followRange * followRange) {
			bison.setAttackTarget(null);
			return false;
		}

		bison.getMoveHelper().setMoveTo(target.posX, target.posY + target.getEyeHeight(), target.posZ, 1);
		bison.getLookHelper().setLookPositionWithEntity(target, 20, 20);

		return true;

	}

}
