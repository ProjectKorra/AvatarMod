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

import com.crowsofwar.avatar.util.analytics.AnalyticEvents;
import com.crowsofwar.avatar.util.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.entity.mob.EntitySkyBison;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAiBisonHelpOwnerTarget extends EntityAITarget {

	private final EntitySkyBison bison;
	EntityLivingBase theTarget;
	private int timestamp;

	public EntityAiBisonHelpOwnerTarget(EntitySkyBison bison) {
		super(bison, false);
		this.bison = bison;
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
			// getRevengeTarget() : entity that was last attacked
			this.theTarget = owner.getRevengeTarget();
			int i = owner.getRevengeTimer();
			return i != this.timestamp && isSuitableTarget(this.theTarget, false);
		}

	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.theTarget);
		EntityLivingBase entitylivingbase = this.bison.getOwner();

		if (entitylivingbase != null) {
			this.timestamp = entitylivingbase.getRevengeTimer();
		}

		// Record analytics
		String targetName = EntityList.getEntityString(theTarget);
		AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onBisonDefend(targetName));

		super.startExecuting();
	}
}
