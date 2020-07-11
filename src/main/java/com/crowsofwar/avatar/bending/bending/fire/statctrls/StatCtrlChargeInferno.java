package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class StatCtrlChargeInferno extends StatusControl {

	public StatCtrlChargeInferno(boolean setting) {
		super(10, setting ? AvatarControl.CONTROL_RIGHT_CLICK_DOWN : AvatarControl.CONTROL_RIGHT_CLICK_UP, CrosshairPosition.RIGHT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		return false;
	}
}
