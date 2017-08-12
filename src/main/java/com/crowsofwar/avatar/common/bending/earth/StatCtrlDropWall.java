package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

/**
 * @author CrowsOfWar
 */
public class StatCtrlDropWall extends StatusControl {

	public StatCtrlDropWall() {
		super(2, AvatarControl.CONTROL_MIDDLE_CLICK_DOWN, CrosshairPosition.BELOW_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {


		return false;
	}

}
