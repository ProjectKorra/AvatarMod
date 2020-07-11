package com.crowsofwar.avatar.bending.bending.fire.powermods;

import com.crowsofwar.avatar.util.data.PowerRatingModifier;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class FireDevourPowerModifier extends PowerRatingModifier {
	private double powerRating;

	public void setPowerRating(double rating) {
		this.powerRating = rating;
	}

	@Override
	public double get(BendingContext ctx) {
		return powerRating;
	}
}
