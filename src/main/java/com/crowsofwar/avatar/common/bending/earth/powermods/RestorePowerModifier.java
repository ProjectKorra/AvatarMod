package com.crowsofwar.avatar.common.bending.earth.powermods;

import com.crowsofwar.avatar.common.bending.BuffPowerModifier;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class RestorePowerModifier extends BuffPowerModifier {

	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData("restore");

		double modifier = abilityData.getLevel() >= 1 ? 40 : 25;
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			modifier = 80;
		}

		return modifier;

	}

	@Override
	protected Vision[] getVisions() {
		//if (CLIENT_CONFIG.shaderSettings.useRestoreShaders) {
			return new Vision[]{Vision.RESTORE_WEAK, Vision.RESTORE_MEDIUM, Vision.RESTORE_POWERFUL};
		//}
		/*else {
			return null;
		}**/
	}

	@Override
	protected String getAbilityName() {
		return "restore";
	}
}

