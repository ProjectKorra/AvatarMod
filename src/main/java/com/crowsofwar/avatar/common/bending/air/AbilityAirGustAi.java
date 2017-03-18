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

import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.getRotationTo;
import static java.lang.Math.toDegrees;

import com.crowsofwar.avatar.common.bending.AbilityAi;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityAirGustAi extends AbilityAi {
	
	/**
	 * @param ability
	 */
	protected AbilityAirGustAi(BendingAbility ability) {
		super(ability);
	}
	
	@Override
	protected void startExec(AbilityContext ctx) {
		
		EntityLivingBase entity = ctx.getBenderEntity();
		EntityLivingBase target = entity.getAITarget();
		BendingData data = ctx.getData();
		
		if (target != null) {
			
			Vector rotations = getRotationTo(getEntityPos(entity), getEntityPos(target));
			entity.rotationYaw = (float) toDegrees(rotations.y());
			entity.rotationPitch = (float) toDegrees(rotations.x());
			
			data.chi().setMaxChi(10);
			data.chi().setTotalChi(10);
			data.chi().setAvailableChi(10);
			
			execAbility(ctx);
			
		}
		
	}
	
}
