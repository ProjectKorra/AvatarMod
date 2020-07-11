package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;

public class AbilityFireRedirect extends Ability {

	public AbilityFireRedirect() {
		super(Firebending.ID, "fire_redirect");
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
