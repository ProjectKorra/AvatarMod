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
package com.crowsofwar.avatar.common.bending.ice;

import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityIceShield;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class StatCtrlShieldShatter extends StatusControl {

	public StatCtrlShieldShatter() {
		super(13, AvatarControl.CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase owner = ctx.getBenderEntity();
		EntityIceShield shield = AvatarEntity.lookupControlledEntity(world, EntityIceShield.class, owner);

		if (shield != null) {
			shield.shatter();
		}

		return true;

	}

}
