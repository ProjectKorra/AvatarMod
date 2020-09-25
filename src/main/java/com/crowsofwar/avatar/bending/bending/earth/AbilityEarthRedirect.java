package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

public class AbilityEarthRedirect extends Ability {

	public AbilityEarthRedirect() {
		super(Earthbending.ID, "earth_redirect");
	}

	@Override
	public void init() {
		super.init();
		addProperties(RADIUS, REDIRECT_TIER, DESTROY_TIER, AIM_ASSIST, RANGE, POWER_BOOST, POWER_DURATION);
	}

	//Creates a shockwave upon use.
	@Override
	public void execute(AbilityContext ctx) {
		super.execute(ctx);
	}

	@Override
	public int getBaseTier() {
		return 3;
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}
}
