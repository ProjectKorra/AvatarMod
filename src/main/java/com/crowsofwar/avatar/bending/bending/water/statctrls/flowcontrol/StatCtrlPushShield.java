package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class StatCtrlPushShield extends StatusControl {

    boolean push;

    //TODO: Update texture
    public StatCtrlPushShield(boolean push) {
        super(20, push ? AvatarControl.CONTROL_LEFT_CLICK_DOWN : AvatarControl.CONTROL_LEFT_CLICK_UP, CrosshairPosition.LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        if (push) {
            //Handle cone explosion code here
            ctx.getData().addStatusControl(StatusControlController.RELEASE_SHIELD_BUBBLE);
        }
        else ctx.getData().addStatusControl(StatusControlController.PUSH_SHIELD_BUBBLE);
        return true;
    }
}
