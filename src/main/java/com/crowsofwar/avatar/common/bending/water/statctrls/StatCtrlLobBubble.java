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

import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;
import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;
import com.crowsofwar.gorecore.util.Vector;

import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.common.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;

/**
 * @author CrowsOfWar
 */
public class StatCtrlLobBubble extends StatusControl {

	/**
	 */
	public StatCtrlLobBubble() {
		super(7, CONTROL_RIGHT_CLICK_DOWN, RIGHT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		BendingData data = ctx.getData();
		double powerRating = ctx.getBender().calcPowerRating(Waterbending.ID);

		EntityWaterBubble bubble = AvatarEntity.lookupEntity(ctx.getWorld(), EntityWaterBubble.class, //
				bub -> bub.getBehavior() instanceof WaterBubbleBehavior.PlayerControlled
						&& bub.getOwner() == ctx.getBenderEntity());

		if (bubble != null) {

			AbilityData adata = data.getAbilityData("water_bubble");
			double speed = adata.getLevel() >= 1 ? 16 : 10;
			if (adata.isMasterPath(AbilityTreePath.FIRST)) {
				speed = 22;
			}
			speed += powerRating / 30f;

			bubble.setBehavior(new WaterBubbleBehavior.Lobbed());
			bubble.addVelocity(bubble.velocity().dividedBy(-1));
			bubble.addVelocity(Vector.getLookRectangular(ctx.getBenderEntity()).times(speed));
		}

		return true;
	}

}
