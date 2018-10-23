package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.BuffPowerModifier;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class CleansePowerModifier extends BuffPowerModifier {

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

	@Override
	protected Vision[] getVisions() {
		//if (CLIENT_CONFIG.shaderSettings.useCleanseShaders) {
		return new Vision[] { Vision.CLEANSE_WEAK, Vision.CLEANSE_MEDIUM, Vision.CLEANSE_POWERFUL };
		//}
		//else return null;
	}

	@Override
	protected String getAbilityName() {
		return "cleanse";
	}

}

