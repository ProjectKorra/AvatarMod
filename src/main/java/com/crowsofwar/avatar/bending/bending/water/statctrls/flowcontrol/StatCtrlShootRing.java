package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

//For shooting mini water arcs from the swirl
public class StatCtrlShootRing extends StatusControl {

    public StatCtrlShootRing(int texture, AvatarControl subscribeTo, CrosshairPosition position) {
        super(texture, subscribeTo, position);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        return false;
    }
}
