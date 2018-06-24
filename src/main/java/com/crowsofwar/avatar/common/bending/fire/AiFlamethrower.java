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

import net.minecraft.entity.*;

import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import static com.crowsofwar.gorecore.util.Vector.*;
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
	public boolean shouldContinueExecuting() {

		if (entity.getAttackTarget() == null) return false;

		Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(entity.getAttackTarget()));
		entity.rotationYaw = (float) toDegrees(rotations.y());
		entity.rotationPitch = (float) toDegrees(rotations.x());

		if (timeExecuting == 20) {

			BendingContext ctx = new BendingContext(bender.getData(), entity, bender, new Raytrace.Result());

			execAbility();
			execStatusControl(StatusControl.START_FLAMETHROW);

		}

		if (timeExecuting > 20) {
			BendingContext ctx = new BendingContext(bender.getData(), entity, bender, new Raytrace.Result());
			TickHandler.FLAMETHROWER.tick(ctx);
		}
		if (timeExecuting >= 80) {
			BendingData data = bender.getData();
			execStatusControl(StatusControl.STOP_FLAMETHROW);
			return false;
		}

		return true;

	}

	@Override
	protected boolean shouldExec() {
		EntityLivingBase target = entity.getAttackTarget();
		return target != null && entity.getDistanceSq(target) < 4 * 4;
	}

	@Override
	protected void startExec() {
	}

}
