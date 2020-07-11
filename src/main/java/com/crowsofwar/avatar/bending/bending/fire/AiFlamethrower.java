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
package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.util.data.StatusControlController.START_FLAMETHROW;
import static com.crowsofwar.avatar.util.data.StatusControlController.STOP_FLAMETHROW;
import static com.crowsofwar.avatar.util.data.TickHandlerController.FLAMETHROWER;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

/**
 * @author CrowsOfWar
 */
public class AiFlamethrower extends BendingAi {

	protected AiFlamethrower(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
		setMutexBits(2);
	}

	@Override
	public void resetTask() {
		super.resetTask();
		bender.getData().removeStatusControl(START_FLAMETHROW);
		bender.getData().removeTickHandler(FLAMETHROWER);
		bender.getData().removeStatusControl(STOP_FLAMETHROW);
	}

	@Override
	public boolean shouldContinueExecuting() {


		if (entity.getAttackTarget() == null || AbilityData.get(bender.getEntity(), "flamethrower").getLevel() < 0) return false;

		Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(entity.getAttackTarget()));
		BendingData data = BendingData.getFromEntity(entity);
		entity.rotationYaw = (float) toDegrees(rotations.y());
		entity.rotationPitch = (float) toDegrees(rotations.x());

		if (timeExecuting == 1) {
			if (!entity.world.isRemote) {
				execStatusControl(START_FLAMETHROW);
			}
		}

		if (timeExecuting >= 120) {
			execStatusControl(STOP_FLAMETHROW);
			bender.getData().removeStatusControl(START_FLAMETHROW);
			bender.getData().removeTickHandler(FLAMETHROWER);

			bender.getData().getAbilityData(ability).setAbilityCooldown(80);
			return false;
		}

		return true;

	}

	@Override
	protected boolean shouldExec() {
		int amount = Math.max(bender.getData().getAbilityData(new AbilityFlamethrower()).getLevel(), 0) + 7;
		EntityLivingBase target = entity.getAttackTarget();
		return target != null && entity.getDistance(target) < amount;
	}

	@Override
	protected void startExec() {
		timeExecuting = 0;
		execAbility();
	}

}
