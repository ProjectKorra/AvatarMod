package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.water.AbilityFlowControl;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

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
            EntityLivingBase entity = ctx.getBenderEntity();
            AbilityFlowControl control = (AbilityFlowControl) Abilities.get("flow_control");
            World world = entity.world;
            BendingData data = ctx.getData();

            if (control != null) {
                AbilityData abilityData = AbilityData.get(entity, "flow_control");
                if (control.getBooleanProperty(AbilityFlowControl.BURST, abilityData)) {
                    EntityWaterBubble bubble = AvatarEntity.lookupControlledEntity(entity.world, EntityWaterBubble.class, entity);
                    //Shockwave toime


                    return true;
                }
            }
            ctx.getData().addStatusControl(StatusControlController.RELEASE_SWIRL_BUBBLE);
        }
        else ctx.getData().addStatusControl(StatusControlController.PUSH_SWIRL_BUBBLE);
        return true;
    }
}
