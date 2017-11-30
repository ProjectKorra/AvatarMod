package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class CleansePowerModifier extends PowerRatingModifier {

	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData("cleanse");

		double modifier = 15;
		if (abilityData.getLevel() >= 2) {
            modifier = 25;
        }
        if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
		    modifier = 40;
        }

		return modifier;

	}

}

