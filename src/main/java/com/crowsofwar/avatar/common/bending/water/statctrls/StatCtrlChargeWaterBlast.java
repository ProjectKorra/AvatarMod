package com.crowsofwar.avatar.common.bending.water.statctrls;

import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class StatCtrlChargeWaterBlast extends StatusControl {

    boolean setting;

    public StatCtrlChargeWaterBlast(boolean setting, int texture) {
        super(texture, setting ? AvatarControl.CONTROL_RIGHT_CLICK_DOWN : AvatarControl.CONTROL_RIGHT_CLICK_UP, CrosshairPosition.RIGHT_OF_CROSSHAIR);
        this.setting = setting;

    }

    @Override
    public boolean execute(BendingContext ctx) {
        return false;
    }
}
