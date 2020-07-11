package com.crowsofwar.avatar.bending.bending.air.powermods;

import com.crowsofwar.avatar.util.data.PowerRatingModifier;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class CloudburstPowerModifier extends PowerRatingModifier {
	@Override
	public double get(BendingContext ctx) {
		return -50;
	}
}
