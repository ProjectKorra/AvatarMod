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
import com.crowsofwar.avatar.bending.bending.BendingAiMelee;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.util.AvatarUtils.normalizeAngle;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.abs;
import static java.lang.Math.toDegrees;

/**
 * @author CrowsOfWar
 */
public class AiFlameStrike extends BendingAiMelee {

	public AiFlameStrike(Ability ability, EntityLiving entity, Bender bender, double speedIn, boolean useLongMemory) {
		super(ability, entity, bender, speedIn, useLongMemory);
	}

	@Override
	public float getMaxTargetRange() {
		float distance = 4;
		distance *= ability.getProperty(Ability.SPEED, bender.getData().getAbilityData(ability)).floatValue() / 5;
		return distance;
	}

	@Override
	public float getMinTargetRange() {
		return 0;
	}

	@Override
	public StatusControl[] getStatusControls() {
		StatusControl[] controls = new StatusControl[2];
		controls[0] = StatusControlController.FLAME_STRIKE_MAIN;
		controls[1] = StatusControlController.FLAME_STRIKE_OFF;
		return controls;
	}

	@Override
	public boolean shouldExecStatCtrl(StatusControl statusControl) {
		return timeExecuting > 0 && timeExecuting % getWaitDuration() == 0;
	}

	@Override
	public int getWaitDuration() {
		return 10;
	}
}