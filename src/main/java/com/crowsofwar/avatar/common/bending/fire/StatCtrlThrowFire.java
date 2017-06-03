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

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowFire extends StatusControl {
	
	public StatCtrlThrowFire() {
		super(6, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(BendingContext ctx) {
		
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		
		EntityFireArc fire = AvatarEntity.lookupEntity(ctx.getWorld(), EntityFireArc.class, //
				arc -> arc.getBehavior() instanceof FireArcBehavior.PlayerControlled
						&& arc.getOwner() == ctx.getBenderEntity());
		
		if (fire != null) {
			
			AbilityData abilityData = data.getAbilityData(BendingAbility.ABILITY_FIRE_ARC);
			
			Vector force = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
					Math.toRadians(entity.rotationPitch));
			force.mul(abilityData.getLevel() >= 1 ? 12 : 8);
			fire.velocity().add(force);
			fire.setBehavior(new FireArcBehavior.Thrown());
			
			return true;
			
		}
		
		return false;
	}
	
}
