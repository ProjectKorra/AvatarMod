package com.crowsofwar.avatar.common.bending.fire;


import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class PurifyPowerModifier extends PowerRatingModifier {
    @Override
    public double get(BendingContext ctx) {

    	BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData("purify");

		double modifier = 20;
		if (abilityData.getLevel() >= 1) {
			modifier = 30;
		}
		if (abilityData.getLevel() == 3) {
			modifier = 60;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			modifier = 100;
		}
		return modifier;

    }

	/**
	 * Different Visions are used as the player levels up to make it seem more powerful. Gets the
	 * appropriate vision to be used for the current level.
	 */
	private Vision getVision(BendingContext ctx) {
		
		AbilityData abilityData = ctx.getData().getAbilityData("purify");
		switch (abilityData.getLevel()) {
			case 0:
			case 1:
				return Vision.PURIFY_WEAK;
			case 2:
				return Vision.PURIFY_MEDIUM;
			case 3:
			default:
				return Vision.PURIFY_POWERFUL;
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
    	if (ctx.getData().getVision() == getVision(ctx)) {
			ctx.getData().setVision(null);
		}
	}

	@Override
	public boolean onUpdate(BendingContext ctx) {

		// Intermittently light on fire
		if (ctx.getBenderEntity().ticksExisted % 20 == 0) {
			// 30% chance per second to be lit on fire
			if (Math.random() < 0.3) {
				ctx.getBenderEntity().setFire(2);
			}
		}

		return super.onUpdate(ctx);
	}
}

