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

import static com.crowsofwar.avatar.common.util.AvatarUtils.normalizeAngle;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.abs;
import static java.lang.Math.toDegrees;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AiFireArc extends BendingAi {
	
	private int timeExecuting;
	
	private float velocityYaw, velocityPitch;
	
	/**
	 * @param ability
	 * @param entity
	 * @param bender
	 */
	protected AiFireArc(BendingAbility ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
		timeExecuting = 0;
		setMutexBits(1);
	}
	
	@Override
	protected void startExec() {
		velocityYaw = 0;
		velocityPitch = 0;
	}
	
	@Override
	public boolean continueExecuting() {
		
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
		
		if (timeExecuting == 20) {
			BendingData data = bender.getData();
			data.chi().setMaxChi(10);
			data.chi().setTotalChi(10);
			data.chi().setAvailableChi(10);
			execAbility();
			data.setAbilityCooldown(80);
		}
		
		if (timeExecuting >= 80) {
			BendingData data = bender.getData();
			execStatusControl(StatusControl.THROW_FIRE);
			timeExecuting = 0;
			return false;
		} else {
			return true;
		}
		
	}
	
	@Override
	public boolean shouldExecute() {
		EntityLivingBase target = entity.getAttackTarget();
		return target != null && entity.getDistanceSqToEntity(target) > 4 * 4
				&& bender.getData().getAbilityCooldown() == 0;
	}
	
	@Override
	public void updateTask() {
		timeExecuting++;
	}
	
}
