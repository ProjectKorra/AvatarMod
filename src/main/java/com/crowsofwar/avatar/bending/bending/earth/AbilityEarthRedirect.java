package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;

public class AbilityEarthRedirect extends Ability {

	public AbilityEarthRedirect() {
		super(Earthbending.ID, "earth_redirect");
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
}
