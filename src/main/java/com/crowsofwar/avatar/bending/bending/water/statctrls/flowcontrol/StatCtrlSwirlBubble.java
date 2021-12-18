package com.crowsofwar.avatar.bending.bending.water.statctrls.flowcontrol;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.water.AbilityCreateWave;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterBubble;
import com.crowsofwar.avatar.entity.data.WaterBubbleBehavior;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class StatCtrlSwirlBubble extends StatusControl {

    public StatCtrlSwirlBubble() {
        super(19, AvatarControl.CONTROL_SHIFT, CrosshairPosition.BELOW_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        EntityWaterBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityWaterBubble.class, entity);
        AbilityCreateWave wave = (AbilityCreateWave) Abilities.get("wave");
        if (bubble != null) {
            bubble.setBehaviour(new WaterBubbleBehavior.StreamShrink());
            data.removeStatusControl(StatusControlController.LOB_BUBBLE);
            data.removeStatusControl(StatusControlController.SHIELD_BUBBLE);
            data.addStatusControl(StatusControlController.RESET_SWIRL_BUBBLE);
            //Unlocking wave grants the ability to push and pull
            if (wave != null && (data.canUse(wave) || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()))
                data.addStatusControl(StatusControlController.PUSH_SWIRL_BUBBLE);
            //Still need smth for water arc
        }
        return true;
    }
}
