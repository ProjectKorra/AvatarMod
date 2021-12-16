package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;

public class StatCtrlPushRing extends StatusControl {

    boolean push;

    //Pushes out the ring; doesn't explode it unless specified
    public StatCtrlPushRing(boolean push) {
        super(20, push ? AvatarControl.CONTROL_RIGHT_CLICK_DOWN : AvatarControl.CONTROL_RIGHT_CLICK_UP, CrosshairPosition.RIGHT_OF_CROSSHAIR);
        this.push = push;
    }

    //Behaviour class handles the actual functionality; I do need to fix textures tho
    @Override
    public boolean execute(BendingContext ctx) {
        if (push) {
            //Handle ring explosion code here (spawning a shockwave or smth)
            ctx.getData().addStatusControl(StatusControlController.RELEASE_SWIRL_BUBBLE);
        }
        else ctx.getData().addStatusControl(StatusControlController.PUSH_SWIRL_BUBBLE);
        return true;
    }
}
