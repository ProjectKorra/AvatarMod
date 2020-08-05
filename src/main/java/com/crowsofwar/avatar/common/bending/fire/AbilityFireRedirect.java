package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;

public class AbilityFireRedirect extends Ability {

	public static final String DESTROY_TIER = "destroyTier", REDIRECT_TIER = "redirectTier",
			ABSORB_FIRE = "absorbFires", ABSORB_TIER = "absorbTier";

	public AbilityFireRedirect() {
		super(Firebending.ID, "fire_redirect");
	}

	@Override
	public void init() {
		super.init();
		addProperties(DESTROY_TIER, REDIRECT_TIER, ABSORB_TIER, RADIUS, AIM_ASSIST, RANGE);
		addBooleanProperties(ABSORB_FIRE);
	}

	@Override
	public int getBaseTier() {
		return 3;
	}

	@Override
	public boolean isVisibleInRadial() {
		return false;
	}

	@Override
	public boolean isUtility() {
		return true;
	}
}
