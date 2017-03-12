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

import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_UP;

import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlSetFlamethrowing extends StatusControl {
	
	private final boolean setting;
	
	public StatCtrlSetFlamethrowing(boolean setting) {
		super(setting ? 4 : 5, setting ? CONTROL_RIGHT_CLICK_DOWN : CONTROL_RIGHT_CLICK_UP,
				RIGHT_OF_CROSSHAIR);
		this.setting = setting;
	}
	
	@Override
	public boolean execute(AbilityContext ctx) {
		
		BendingData data = ctx.getData();
		EntityLivingBase bender = ctx.getBenderEntity();
		World world = ctx.getWorld();
		
		if (data.hasBending(BendingType.FIREBENDING)) {
			if (setting) {
				data.addStatusControl(STOP_FLAMETHROW);
				data.addTickHandler(TickHandler.FLAMETHROWER);
			} else {
				data.removeTickHandler(TickHandler.FLAMETHROWER);
			}
		}
		
		return true;
	}
	
}
