package com.crowsofwar.avatar.bending.bending.air.powermods;

import com.crowsofwar.avatar.util.data.PowerRatingModifier;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

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
