package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.TickHandlerController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class StatCtrlChargeEarthspike extends StatusControl {

    private boolean setting;

    public StatCtrlChargeEarthspike(boolean setting) {
        super(8, setting ? AvatarControl.CONTROL_RIGHT_CLICK_DOWN : AvatarControl.CONTROL_RIGHT_CLICK_UP,
                CrosshairPosition.RIGHT_OF_CROSSHAIR);
        this.setting = setting;
    }

    @Override
    public boolean execute(BendingContext ctx) {
        BendingData data = ctx.getData();

        if (setting)
            data.addTickHandler(TickHandlerController.RELEASE_EARTHSPIKE, ctx);
        else data.removeTickHandler(TickHandlerController.RELEASE_EARTHSPIKE, ctx);

        return true;
    }
}
