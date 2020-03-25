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

package com.crowsofwar.avatar.common.bending.air.statctrls;

import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

import static com.crowsofwar.avatar.common.bending.air.tickhandlers.AirBurstHandler.AIRBURST_MOVEMENT_MODIFIER_ID;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_UP;
import static com.crowsofwar.avatar.common.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.data.StatusControlController.RELEASE_AIR_BURST;
import static com.crowsofwar.avatar.common.data.StatusControlController.SHOOT_AIR_BURST;
import static com.crowsofwar.avatar.common.data.TickHandlerController.AIRBURST_CHARGE_HANDLER;

/**
 * @author CrowsOfWar
 */
public class StatCtrlSetAirburstCharging extends StatusControl {

	private final boolean setting;

	public StatCtrlSetAirburstCharging(boolean setting) {
		super(setting ? 11 : 12, setting ? CONTROL_RIGHT_CLICK_DOWN : CONTROL_RIGHT_CLICK_UP,
				RIGHT_OF_CROSSHAIR);
		this.setting = setting;
	}

	@Override
	public boolean execute(BendingContext ctx) {

		BendingData data = ctx.getData();
		EntityLivingBase bender = ctx.getBenderEntity();

		if (data.hasBendingId(Airbending.ID)) {
			if (setting) {
				data.addStatusControl(RELEASE_AIR_BURST);
				data.addTickHandler(AIRBURST_CHARGE_HANDLER);
			} else {
				if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(AIRBURST_MOVEMENT_MODIFIER_ID) != null)
					bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(AIRBURST_MOVEMENT_MODIFIER_ID);
				data.removeStatusControl(SHOOT_AIR_BURST);
				//We don't remove the status control here since we want to spawn the tick handler if we stop right clicking.
			}
		}

		return true;
	}

}
