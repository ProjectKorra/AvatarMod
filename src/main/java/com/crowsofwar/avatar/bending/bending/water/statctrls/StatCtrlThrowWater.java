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

package com.crowsofwar.avatar.bending.bending.water.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterArc;
import com.crowsofwar.avatar.entity.data.WaterArcBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class StatCtrlThrowWater extends StatusControl {

	public StatCtrlThrowWater() {
		super(3, AvatarControl.CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		World world = ctx.getWorld();
		AbilityData abilityData = data.getAbilityData("water_arc");

		int lvl = abilityData.getLevel();
		double velocity = 14;

		if (lvl == 1) {
			velocity = 16;
		}
		if (lvl == 2) {
			velocity = 18;
		}
		if (lvl == 3) {
			velocity = 22;
		}

		EntityWaterArc arc = AvatarEntity.lookupEntity(ctx.getWorld(), EntityWaterArc.class, //
				water -> water.getBehavior() instanceof WaterArcBehavior.PlayerControlled
						&& water.getOwner() == ctx.getBenderEntity());


		if (arc != null) {

			Vector force = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
					Math.toRadians(entity.rotationPitch));
			force = force.times(velocity);
			arc.addVelocity(arc.velocity().dividedBy(-1));
			arc.addVelocity(force);
			arc.setBehavior(new WaterArcBehavior.Thrown());

		}

		return true;

	}

}
