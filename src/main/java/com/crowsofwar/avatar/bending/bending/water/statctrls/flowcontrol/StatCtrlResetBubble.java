package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class StatCtrlResetBubble extends StatusControl {

    //Used for determining what type of reset; nothing else
    private final EntityWaterBubble.State state;

    public StatCtrlResetBubble(AvatarControl control, CrosshairPosition position, EntityWaterBubble.State state) {
        //Either shield or swirl. No bubble reset, so I only need a ternary operator here
        super(state == EntityWaterBubble.State.SHIELD ? 36 : 19, control, position);
        this.state = state;
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();

        EntityWaterBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityWaterBubble.class, entity);
        if (bubble != null) {
            //The behaviour immediately changes the state, so change the stat ctrl first
            if (state.equals(EntityWaterBubble.State.SHIELD))
                data.addStatusControl(StatusControlController.SHIELD_BUBBLE);
            if (state.equals(EntityWaterBubble.State.STREAM))
                data.addStatusControl(StatusControlController.SWIRL_BUBBLE);
            //Removes old stat ctrls
            data.removeStatusControl(StatusControlController.PUSH_SWIRL_BUBBLE);
            data.removeStatusControl(StatusControlController.RELEASE_SWIRL_BUBBLE);
            data.removeStatusControl(StatusControlController.PUSH_SHIELD_BUBBLE);
            data.removeStatusControl(StatusControlController.RELEASE_SHIELD_BUBBLE);
            bubble.setBehaviour(new WaterBubbleBehavior.Grow());
        }


        return true;
    }
}
