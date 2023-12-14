package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.water.AbilityFlowControl;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class StatCtrlPushShield extends StatusControl {

    boolean push;

    //TODO: Update texture
    public StatCtrlPushShield(boolean push) {
        super(20, push ? AvatarControl.CONTROL_LEFT_CLICK_DOWN : AvatarControl.CONTROL_LEFT_CLICK_UP, CrosshairPosition.LEFT_OF_CROSSHAIR);
        this.push = push;
    }

    @Override
    public boolean execute(BendingContext ctx) {
        if (push) {
            AbilityFlowControl control = (AbilityFlowControl) Abilities.get("flow_control");
            AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "flow_control");
            if (control != null) {
                //Handle cone explosion code here
                EntityWaterBubble bubble = AvatarEntity.lookupControlledEntity(ctx.getWorld(), EntityWaterBubble.class,
                        ctx.getBenderEntity());
                if (control.getBooleanProperty(AbilityFlowControl.BURST, abilityData)) {
                    if (bubble != null)
                        bubble.setBehaviour(new WaterBubbleBehavior.Explode());
                } else
                    ctx.getData().addStatusControl(StatusControlController.RELEASE_SHIELD_BUBBLE);
            }
        } else ctx.getData().addStatusControl(StatusControlController.PUSH_SHIELD_BUBBLE);
        return true;
    }
}
