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
package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.common.util.AvatarUtils.normalizeAngle;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.abs;
import static java.lang.Math.toDegrees;

/**
 * @author CrowsOfWar
 */
public class AiFlameStrike extends BendingAi {

	private int timeExecuting;

	private float velocityYaw, velocityPitch;

	/**
	 * @param ability
	 * @param entity
	 * @param bender
	 */
	protected AiFlameStrike(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
		timeExecuting = 0;
		setMutexBits(2);
	}

	@Override
	protected void startExec() {
		BendingData data = bender.getData();
		velocityYaw = 0;
		velocityPitch = 0;
		execAbility();
	}

	@Override
	public boolean shouldContinueExecuting() {

		if (entity.getAttackTarget() == null) return false;

		Vector target = getRotationTo(getEntityPos(entity), getEntityPos(entity.getAttackTarget()));
		float targetYaw = (float) toDegrees(target.y());
		float targetPitch = (float) toDegrees(target.x());

		float currentYaw = normalizeAngle(entity.rotationYaw);
		float currentPitch = normalizeAngle(entity.rotationPitch);

		float yawLeft = abs(normalizeAngle(currentYaw - targetYaw));
		float yawRight = abs(normalizeAngle(targetYaw - currentYaw));
		if (yawRight < yawLeft) {
			velocityYaw += yawRight / 10;
		} else {
			velocityYaw -= yawLeft / 10;
		}

		entity.rotationYaw += velocityYaw;
		entity.rotationPitch += velocityPitch;

		if (timeExecuting < 20) {
			entity.rotationYaw = targetYaw;
			entity.rotationPitch = targetPitch;
		}


		if (timeExecuting >= 20) {
			BendingData data = bender.getData();
			//execStatusControl(THROW_FIRE);
			timeExecuting = 0;
			return false;
		} else {
			return true;
		}

	}

	@Override
	protected boolean shouldExec() {
		EntityLivingBase target = entity.getAttackTarget();
		return target != null && entity.getDistanceSq(target) < 3 * 3
				&& bender.getData().getAbilityData(ability).getAbilityCooldown() == 0;
	}

	@Override
	public void updateTask() {
		timeExecuting++;
	}

	@Override
	public void resetTask() {


	}

}