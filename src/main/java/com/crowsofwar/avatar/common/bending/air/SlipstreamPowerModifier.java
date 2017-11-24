package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class SlipstreamPowerModifier extends PowerRatingModifier {

	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(new AbilitySlipstream());

		double modifier = 20 + 8 * abilityData.getLevel();
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			modifier = 60;
		}

		return modifier;

	}

}

