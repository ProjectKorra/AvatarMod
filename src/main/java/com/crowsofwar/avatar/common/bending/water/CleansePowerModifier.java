package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.Vision;
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

	/**
	 * Different Visions are used as the player levels up to make it seem more powerful. Gets the
	 * appropriate vision to be used for the current level.
	 */
	private Vision getVision(BendingContext ctx) {

		AbilityData abilityData = ctx.getData().getAbilityData("cleanse");
		switch (abilityData.getLevel()) {
			case 0:
			case 1:
				return Vision.CLEANSE_WEAK;
			case 2:
				return Vision.CLEANSE_MEDIUM;
			case 3:
			default:
				return Vision.CLEANSE_POWERFUL;
		}

	}

	@Override
	public void onAdded(BendingContext ctx) {
		if (ctx.getData().getVision() == null) {
			ctx.getData().setVision(getVision(ctx));
		}
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		Vision vision = ctx.getData().getVision();
		if (vision != null && vision.name().startsWith("CLEANSE")) {
			ctx.getData().setVision(null);
		}
	}

}

