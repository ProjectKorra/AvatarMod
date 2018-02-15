package com.crowsofwar.avatar.common.bending.avatar;

import com.crowsofwar.avatar.common.bending.BuffPowerModifier;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class AvatarStatePowerModifier extends BuffPowerModifier {
	@Override
	protected Vision[] getVisions() {
		return new Vision[0];
	}

	@Override
	protected String getAbilityName() {
		return "avatar_state";
	}

	@Override
	public double get(BendingContext ctx) {
		return 0;
	}
}
