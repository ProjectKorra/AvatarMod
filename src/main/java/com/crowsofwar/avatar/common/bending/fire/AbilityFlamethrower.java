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

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author CrowsOfWar
 */
public class AbilityFlamethrower extends Ability {

	public AbilityFlamethrower() {
		super(Firebending.ID, "flamethrower");
	}

	@Override
	public void execute(AbilityContext ctx) {
		BendingData data = ctx.getData();
		data.addStatusControl(StatusControl.START_FLAMETHROW);
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFlamethrower(this, entity, bender);
	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();

		int coolDown = 80;

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}

		if (ctx.getLevel() == 1) {
			coolDown = 60;
		}
		if (ctx.getLevel() == 2) {
			coolDown = 40;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 30;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 25;
		}
		return coolDown;
	}
}
