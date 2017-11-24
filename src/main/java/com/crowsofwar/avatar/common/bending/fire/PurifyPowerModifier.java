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
        AbilityData abilityData = data.getAbilityData(new AbilityPurify());
        return 10+(3*abilityData.getLevel());

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
		ctx.getBenderEntity().setFire(1);
		return super.onUpdate(ctx);
	}
}

