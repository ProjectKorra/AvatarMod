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

import com.crowsofwar.avatar.common.analytics.AnalyticEvents;
import com.crowsofwar.avatar.common.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAiBisonDefendOwner extends EntityAITarget {

	private final EntitySkyBison bison;
	private EntityLivingBase ownersTarget;
	private int timestamp;

	public EntityAiBisonDefendOwner(EntitySkyBison bison) {
		super(bison, false);
		this.bison = bison;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {

		EntityLivingBase owner = this.bison.getOwner();

		if (owner == null) {
			return false;
		} else {
			// getAITarget() returns last entity that attacked the player
			this.ownersTarget = owner.getAttackingEntity();
			int i = owner.getRevengeTimer();
			return i != this.timestamp && this.isSuitableTarget(this.ownersTarget, false);
		}

	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.ownersTarget);
		EntityLivingBase entitylivingbase = this.bison.getOwner();

		if (entitylivingbase != null) {
			this.timestamp = entitylivingbase.getRevengeTimer();
		}

		// Record analytics
		String targetName = EntityList.getEntityString(ownersTarget);
		AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onBisonDefend(targetName));

		super.startExecuting();
	}

}
