package com.crowsofwar.avatar.bending.bending.water.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class StatCtrlThrowBubble extends StatusControl {

	public StatCtrlThrowBubble(int texture, AvatarControl subscribeTo, CrosshairPosition position) {
		super(19, AvatarControl.CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		return false;
	}
}
