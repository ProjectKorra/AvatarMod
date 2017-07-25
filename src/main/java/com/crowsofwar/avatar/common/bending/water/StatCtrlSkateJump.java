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
package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;

import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.BELOW_CROSSHAIR;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_JUMP;

/**
 * 
 * 
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
		if (data.hasTickHandler(TickHandler.WATER_SKATE)) {
			data.removeTickHandler(TickHandler.WATER_SKATE);
			
			Vector velocity = Vector.getLookRectangular(entity).times(1.5);
			entity.motionX = velocity.x() * 2;
			entity.motionY = velocity.y();
			entity.motionZ = velocity.z() * 2;
			AvatarUtils.afterVelocityAdded(entity);
			
			data.setFallAbsorption(6);
			AbilityData abilityData = data.getAbilityData(AbilityWaterSkate.ID);
			if (abilityData.isMasterPath(AbilityTreePath.SECOND)) {
				data.addTickHandler(TickHandler.SMASH_GROUND);
			}
			
		}
		
		return true;
	}
	
}
