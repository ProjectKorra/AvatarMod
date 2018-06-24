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

import net.minecraft.entity.*;

import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.gorecore.util.Vector;

import static com.crowsofwar.gorecore.util.Vector.*;
import static java.lang.Math.toDegrees;

/**
 * @author CrowsOfWar
 */
public class AiAirGust extends BendingAi {

	/**
	 * @param ability
	 * @param entity
	 * @param bender
	 */
	protected AiAirGust(Ability ability, EntityLiving entity, Bender bender) {
		super(ability, entity, bender);
	}

	@Override
	protected void startExec() {
		// EntityLivingBase entity = ctx.getBenderEntity();
		EntityLivingBase target = entity.getAttackTarget();
		BendingData data = bender.getData();

		if (target != null && target.getHealth() >= 10) {

			Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(target));
			entity.rotationYaw = (float) toDegrees(rotations.y());
			entity.rotationPitch = (float) toDegrees(rotations.x());

			data.chi().setMaxChi(10);
			data.chi().setTotalChi(10);
			data.chi().setAvailableChi(10);

			execAbility();
			data.getMiscData().setAbilityCooldown(20);

		}
	}

	@Override
	protected boolean shouldExec() {
		return entity.getAttackTarget() != null && entity.getDistanceSq(entity.getAttackTarget()) < 4 * 4;
	}

}
