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

	public double getPowerrating() {
		return this.powerrating;
	}

	public int getTicks() {
		return this.ticks;
	}

	@Override
	public double get(BendingContext ctx) {
		return powerrating;
	}

	@Override
	public void onAdded(BendingContext ctx) {
		super.onAdded(ctx);
		System.out.println("Nice");
	}

	@Override
	public void onRemoval(BendingContext ctx) {
		super.onRemoval(ctx);
		System.out.println("Hm");
	}
}
