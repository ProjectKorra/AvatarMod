package com.crowsofwar.avatar.common.bending.air.powermods;

import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class CloudburstPowerModifier extends PowerRatingModifier {
	@Override
	public double get(BendingContext ctx) {
		return -50;
	}
}
