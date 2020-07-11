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

import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;
import net.minecraft.entity.EntityLivingBase;

import java.util.List;

/**
 * @author CrowsOfWar
 */
public class StatCtrlBubbleContract extends StatusControl {

	public StatCtrlBubbleContract() {
		super(12, AvatarControl.CONTROL_RIGHT_CLICK, CrosshairPosition.RIGHT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();

		List<EntityAirBubble> entities = entity.world.getEntitiesWithinAABB(EntityAirBubble.class,
				entity.getEntityBoundingBox(), bubble -> bubble.getOwner() == entity);
		for (EntityAirBubble bubble : entities) {
			bubble.dissipateSmall();
		}

		return true;
	}

}
