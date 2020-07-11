package com.crowsofwar.avatar.bending.bending.water.tickhandlers;

import com.crowsofwar.avatar.bending.bending.BuffPowerModifier;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.Vision;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class CleansePowerModifier extends BuffPowerModifier {

	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData("cleanse");

		double modifier = 50;
		if (abilityData.getLevel() >= 2) {
			modifier = 70;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			modifier = 100;
		}

		return modifier;

	}

	@Override
	protected Vision[] getVisions() {
		//if (CLIENT_CONFIG.shaderSettings.useCleanseShaders) {
		return new Vision[]{Vision.CLEANSE_WEAK, Vision.CLEANSE_MEDIUM, Vision.CLEANSE_POWERFUL};
		//}
		//else return null;
	}

	@Override
	protected String getAbilityName() {
		return "cleanse";
	}

}

