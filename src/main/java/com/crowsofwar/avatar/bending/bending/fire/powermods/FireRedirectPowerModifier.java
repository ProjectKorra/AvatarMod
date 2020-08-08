package com.crowsofwar.avatar.bending.bending.fire.powermods;

import com.crowsofwar.avatar.util.data.PowerRatingModifier;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class FireRedirectPowerModifier extends PowerRatingModifier {

	//Unecessary for a powerrating boolean, as I can just add a new instance of this every time something is absorbed.
	//Actually, that's not true. Woops (dynamic powerrating based on projectile absorbed).

	private double powerrating;

	public FireRedirectPowerModifier() {
		super();
		this.powerrating = 5;
	}

	public void setPowerRating(double powerrating) {
		this.powerrating = powerrating;
	}

	@Override
	public double get(BendingContext ctx) {
		return powerrating;
	}


}
