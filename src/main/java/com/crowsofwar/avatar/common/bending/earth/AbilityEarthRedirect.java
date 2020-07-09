package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;

public class AbilityEarthRedirect extends Ability {

	public AbilityEarthRedirect() {
		super(Earthbending.ID, "earth_redirect");
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
