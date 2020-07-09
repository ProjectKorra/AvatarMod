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
package com.crowsofwar.avatar.common.bending.water.statctrls;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_JUMP;
import static com.crowsofwar.avatar.common.data.StatusControl.CrosshairPosition.BELOW_CROSSHAIR;
import static com.crowsofwar.avatar.common.data.TickHandlerController.SMASH_GROUND_WATER;
import static com.crowsofwar.avatar.common.data.TickHandlerController.WATER_SKATE;

/**
 * @author CrowsOfWar
 */
public class StatCtrlSkateJump extends StatusControl {

	public StatCtrlSkateJump() {
		super(9, CONTROL_JUMP, BELOW_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		BendingData data = ctx.getData();
		EntityLivingBase entity = ctx.getBenderEntity();
		if (data.hasTickHandler(WATER_SKATE)) {
			data.removeTickHandler(WATER_SKATE);
			data.getMiscData().setCanUseAbilities(true);

			Vector velocity = Vector.getLookRectangular(entity).times(1.5);
			entity.motionX = velocity.x();
			entity.motionY = velocity.y();
			entity.motionZ = velocity.z();
			AvatarUtils.afterVelocityAdded(entity);

			data.getMiscData().setFallAbsorption(9);

			AbilityData abilityData = data.getAbilityData("water_skate");
			if (abilityData.isMasterPath(AbilityTreePath.SECOND)) {
				data.addTickHandler(SMASH_GROUND_WATER);
			}

		}

		return true;
	}

}
