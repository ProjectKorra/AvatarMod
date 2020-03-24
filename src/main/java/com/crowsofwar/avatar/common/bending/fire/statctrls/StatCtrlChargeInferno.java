package com.crowsofwar.avatar.common.bending.fire.statctrls;

import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class StatCtrlChargeInferno extends StatusControl {

	public StatCtrlChargeInferno(int texture, AvatarControl subscribeTo, CrosshairPosition position) {
		super(texture, subscribeTo, position);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		return false;
	}
}
