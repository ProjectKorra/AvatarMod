package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;

import java.util.UUID;

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
}
