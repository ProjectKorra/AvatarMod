package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;

public class AbilityBubbleBlitz extends Ability {

	public AbilityBubbleBlitz() {
		super(Waterbending.ID, "bubble_blitz");
	}

	@Override
	public void execute(AbilityContext ctx) {

	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public int getBaseTier() {
		return 4;
	}
}
