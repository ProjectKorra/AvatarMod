package com.crowsofwar.avatar.common.bending.water.statctrls;

import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class StatCtrlThrowBubble extends StatusControl {

	public StatCtrlThrowBubble(int texture, AvatarControl subscribeTo, CrosshairPosition position) {
		super(19, AvatarControl.CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		return false;
	}
}
