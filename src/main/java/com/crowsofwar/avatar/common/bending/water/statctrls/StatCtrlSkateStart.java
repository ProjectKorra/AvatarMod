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

import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

import static com.crowsofwar.avatar.common.data.StatusControl.CrosshairPosition.BELOW_CROSSHAIR;

/**
 * @author CrowsOfWar
 */
public class StatCtrlSkateStart extends StatusControl {

	public StatCtrlSkateStart() {
		super(8, AvatarControl.CONTROL_SHIFT, BELOW_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		return true;
	}

}
