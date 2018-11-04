package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class StatCtrlChargeBubble extends StatusControl {
	public StatCtrlChargeBubble(int texture, AvatarControl subscribeTo, CrosshairPosition position) {
		super(20, AvatarControl.CONTROL_SHIFT, CrosshairPosition.ABOVE_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		return false;
	}
}
