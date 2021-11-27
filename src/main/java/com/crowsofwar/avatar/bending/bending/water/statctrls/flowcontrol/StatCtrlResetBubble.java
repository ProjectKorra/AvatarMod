package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class StatCtrlResetBubble extends StatusControl {

    public StatCtrlResetBubble(AvatarControl control, CrosshairPosition position) {
        super(20, control, position);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();

        EntityWaterBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityWaterBubble.class, entity);
        if (bubble != null) {
            bubble.setState(EntityWaterBubble.State.BUBBLE);
            if (data.hasStatusControl(StatusControlController.RESET_SHIELD_BUBBLE))
                data.addStatusControl(StatusControlController.SHIELD_BUBBLE);
//        if (data.hasStatusControl(StatusControlController.RESET_SWIRL_BUBBLE))
//            data.addStatusControl(StatusControl.);
        }


        return true;
    }
}
