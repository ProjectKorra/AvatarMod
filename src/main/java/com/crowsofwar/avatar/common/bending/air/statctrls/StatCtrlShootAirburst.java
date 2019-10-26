package com.crowsofwar.avatar.common.bending.air.statctrls;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class StatCtrlShootAirburst extends StatusControl {

	public StatCtrlShootAirburst() {
		super(17, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		return false;
	}
}
