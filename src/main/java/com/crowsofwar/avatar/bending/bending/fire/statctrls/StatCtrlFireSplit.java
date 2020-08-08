package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class StatCtrlFireSplit extends StatusControl {

    public StatCtrlFireSplit() {
        //Automatically disables itself while status controls with similar controls are in use.
        super(100, AvatarControl.CONTROL_RIGHT_CLICK, CrosshairPosition.RIGHT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        return false;
    }
}
