package com.crowsofwar.avatar.common.bending.air.powermods;

import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

/**
 * @author CrowsOfWar
 */
public class AirJumpPowerModifier extends PowerRatingModifier {

	private final double amount;

	public AirJumpPowerModifier(double amount) {
		this.amount = amount;
	}

	@Override
	public double get(BendingContext ctx) {
		return amount;
	}

}
