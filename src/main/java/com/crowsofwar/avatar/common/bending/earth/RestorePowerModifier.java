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

		double modifier = abilityData.getLevel() >= 1 ? 40 : 25;
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			modifier = 80;
		}

		return modifier;

	}

	/**
	 * Different Visions are used as the player levels up to make it seem more powerful. Gets the
	 * appropriate vision to be used for the current level.
	 */
	private Vision getVision(BendingContext ctx) {

		AbilityData abilityData = ctx.getData().getAbilityData("restore");
		switch (abilityData.getLevel()) {
			case 0:
			case 1:
				return Vision.RESTORE_WEAK;
			case 2:
				return Vision.RESTORE_MEDIUM;
			case 3:
			default:
				return Vision.RESTORE_POWERFUL;
		}

	}

	@Override
	public void onAdded(BendingContext ctx) {
		if (ctx.getData().getVision() == null) {
			ctx.getData().setVision(getVision(ctx));
		}
		super.onAdded(ctx);
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		Vision vision = ctx.getData().getVision();
		if (vision != null && vision.name().startsWith("RESTORE")) {
			ctx.getData().setVision(null);
		}
		super.onRemoval(ctx);
	}

}

