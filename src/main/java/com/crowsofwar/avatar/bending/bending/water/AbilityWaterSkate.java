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
package com.crowsofwar.avatar.bending.bending.water;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

import static com.crowsofwar.avatar.util.data.StatusControlController.SKATING_START;
import static com.crowsofwar.avatar.util.data.TickHandlerController.WATER_SKATE;

/**
 * @author CrowsOfWar
 */
public class AbilityWaterSkate extends Ability {

	public static String
			RUN_ON_LAND = "runOnLand",
			USE_ABILITIES = "useAbilities",
			PUSH_AWAY = "pushAway";

	public AbilityWaterSkate() {
		super(Waterbending.ID, "water_skate");
	}

	@Override
	public void init() {
		super.init();
		addProperties(TRAVEL_SPEED);
		addBooleanProperties(RUN_ON_LAND, USE_ABILITIES, PUSH_AWAY);
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {
		BendingData data = ctx.getData();
		data.addStatusControl(SKATING_START);
		ctx.getData().addTickHandler(WATER_SKATE, ctx);
	}

	@Override
	public int getBaseTier() {
		return 2;
	}
}
