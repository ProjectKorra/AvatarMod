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

	@Override
	public void onAdded(BendingContext ctx) {
		if (ctx.getData().getVision() == null) {
			ctx.getData().setVision(Vision.PURIFY);
		}
	}

	@Override
	public void onRemoval(BendingContext ctx) {
    	if (ctx.getData().getVision() == Vision.PURIFY) {
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

