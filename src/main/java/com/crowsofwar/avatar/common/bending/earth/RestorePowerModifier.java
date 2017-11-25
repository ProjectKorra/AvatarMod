package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class RestorePowerModifier extends PowerRatingModifier {

	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData("restore");

		double modifier = abilityData.getLevel() >= 1 ? 25 : 15;
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			modifier = 40;
		}

		return modifier;

	}

	@Override
	public void onAdded(BendingContext ctx) {
		if (ctx.getData().getVision() == null) {
			ctx.getData().setVision(Vision.RESTORE);
		}
		super.onAdded(ctx);
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		if (ctx.getData().getVision().name().startsWith("RESTORE")) {
			ctx.getData().setVision(null);
		}
		super.onRemoval(ctx);
	}

}

