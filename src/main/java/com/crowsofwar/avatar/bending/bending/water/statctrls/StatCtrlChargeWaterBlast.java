package com.crowsofwar.avatar.bending.bending.water.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

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
