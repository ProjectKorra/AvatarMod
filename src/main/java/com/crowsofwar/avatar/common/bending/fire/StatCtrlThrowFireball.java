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

import static com.crowsofwar.avatar.common.bending.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatCtrlThrowFireball extends StatusControl {
	
	public StatCtrlThrowFireball() {
		super(10, CONTROL_LEFT_CLICK, LEFT_OF_CROSSHAIR);
	}
	
	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		
		double size = 6;
		
		EntityFireball fireball = AvatarEntity.lookupControlledEntity(world, EntityFireball.class, entity);
		
		if (fireball != null) {
			AbilityData abilityData = ctx.getData().getAbilityData(AbilityFireball.ID);
			double speedMult = abilityData.getLevel() >= 1 ? 25 : 15;
			fireball.velocity().add(Vector.getLookRectangular(entity).mul(speedMult));
			fireball.setBehavior(new FireballBehavior.Thrown());
		}
		
		return true;
	}
	
}
