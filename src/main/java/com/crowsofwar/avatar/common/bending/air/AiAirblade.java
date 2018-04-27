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
package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

/**
 * @author CrowsOfWar
 */
public class AiAirblade extends BendingAi {

	/**
	 * @param ability
	 * @param entity
	 * @param bender
	 */
	protected AiAirblade(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
	}

	@Override
	protected void startExec() {
		EntityLivingBase target = entity.getAttackTarget();
		Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(target));
		entity.rotationYaw = (float) toDegrees(rotations.y());
		entity.rotationPitch = (float) toDegrees(rotations.x());
	}

	@Override
	public boolean shouldContinueExecuting() {
		entity.rotationYaw = entity.rotationYawHead;
		if (timeExecuting >= 40 && entity.getAttackTarget() != null) {
			Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(entity.getAttackTarget()));
			entity.rotationYaw = (float) toDegrees(rotations.y());
			entity.rotationPitch = (float) toDegrees(rotations.x());
			execAbility();
			return false;
		}
		return true;
	}

	@Override
	protected boolean shouldExec() {

		EntityLivingBase target = entity.getAttackTarget();

		if (target != null) {
			double dist = entity.getDistanceSq(target);
			return dist >= 4 * 4;
		}

		return false;

	}

}
