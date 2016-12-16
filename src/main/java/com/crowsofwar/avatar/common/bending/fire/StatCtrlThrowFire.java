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

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowFire extends StatusControl {
	
	public StatCtrlThrowFire() {
		super(6, AvatarControl.CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(AbilityContext context) {
		
		EntityPlayer player = context.getPlayerEntity();
		AvatarPlayerData data = context.getData();
		
		FirebendingState bendingState = (FirebendingState) data
				.getBendingState(BendingManager.getBending(BendingType.FIREBENDING));
		
		if (bendingState.isManipulatingFire()) {
			
			EntityFireArc fire = bendingState.getFireArc();
			
			Vector force = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
					Math.toRadians(player.rotationPitch));
			force.mul(10);
			fire.velocity().add(force);
			fire.setBehavior(new FireArcBehavior.Thrown());
			
			bendingState.setNoFireArc();
			data.sendBendingState(bendingState);
			
			return true;
			
		}
		
		return false;
	}
	
}
