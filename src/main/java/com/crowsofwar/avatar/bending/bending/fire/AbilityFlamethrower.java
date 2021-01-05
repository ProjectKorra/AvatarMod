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

package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLiving;

import static com.crowsofwar.avatar.util.data.StatusControlController.START_FLAMETHROW;

/**
 * @author CrowsOfWar, FavouriteDragon
 * TODO: Optimise particles
 */
public class AbilityFlamethrower extends Ability {

	public static final String
			RANDOMNESS = "randomness",
			FLAMES_PER_SECOND = "particles";

	public AbilityFlamethrower() {
		super(Firebending.ID, "flamethrower");
	}

	@Override
	public void execute(AbilityContext ctx) {
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		if (bender.consumeChi(getChiCost(ctx) / 4)) {
			data.addStatusControl(START_FLAMETHROW);
			ctx.getAbilityData().setRegenBurnout(false);
		}
	}

	@Override
	public void init() {
		super.init();
		addProperties(FIRE_R, FIRE_G, FIRE_B, FADE_R, FADE_G, FADE_B, RANDOMNESS,  FLAMES_PER_SECOND);
		addBooleanProperties(SMELTS, SETS_FIRES);
	}

	@Override
	public int getBaseTier() {
		return 2;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFlamethrower(this, entity, bender);
	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		return 0;
	}

	@Override
	public float getBurnOut(AbilityContext ctx) {
		return 0;
	}

	@Override
	public float getExhaustion(AbilityContext ctx) {
		return 0;
	}


}
