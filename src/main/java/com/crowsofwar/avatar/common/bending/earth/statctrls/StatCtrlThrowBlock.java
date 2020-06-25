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

package com.crowsofwar.avatar.common.bending.earth.statctrls;

import com.crowsofwar.avatar.common.bending.earth.AbilityEarthControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.data.StatusControlController.PLACE_BLOCK;

/**
 * @author CrowsOfWar
 */
public class StatCtrlThrowBlock extends StatusControl {

	public StatCtrlThrowBlock() {
		super(2, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		World world = entity.world;
		BendingData data = ctx.getData();
		AbilityData abilityData = AbilityData.get(entity, new AbilityEarthControl().getName());

		EntityFloatingBlock floating = AvatarEntity.lookupControlledEntity(world, EntityFloatingBlock.class,
				entity);

		if (floating != null) {

			float yaw = (float) Math.toRadians(entity.rotationYaw);
			float pitch = (float) Math.toRadians(entity.rotationPitch);

			// Calculate force and everything
			double forceMult = 20;
			if (abilityData.getLevel() == 1)
				forceMult += 8;
			if (abilityData.getLevel() == 2)
				forceMult += 14;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST))
				forceMult += 16;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND))
				forceMult += 30;

			Vector lookDir = Vector.toRectangular(yaw, pitch);
			floating.setVelocity(lookDir.times(forceMult));
			floating.setBehavior(new FloatingBlockBehavior.Thrown());

			data.removeStatusControl(PLACE_BLOCK);

			return true;

		}

		return false;

	}

}
