package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class StatCtrlPushRing extends StatusControl {

    public StatCtrlPushRing(int texture, AvatarControl subscribeTo, CrosshairPosition position) {
        super(texture, subscribeTo, position);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        return false;
    }
}
