package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

/**
 * @author CrowsOfWar
 */
public class AirJumpPowerModifier extends PowerRatingModifier {

	@Override
	public double get(BendingContext ctx) {
		return 10;
	}

}
