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

public class StatCtrlShieldBubble extends StatusControl {


    public StatCtrlShieldBubble() {
        super(32, AvatarControl.CONTROL_RIGHT_CLICK_DOWN, CrosshairPosition.RIGHT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        EntityWaterBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityWaterBubble.class, entity);
        AbilityCreateWave wave = (AbilityCreateWave) Abilities.get("wave");

        if (bubble != null) {
            bubble.setBehaviour(new WaterBubbleBehavior.ShieldShrink());
            data.removeStatusControl(StatusControlController.SHIELD_BUBBLE);
            data.removeStatusControl(StatusControlController.LOB_BUBBLE);
            data.addStatusControl(StatusControlController.RESET_SHIELD_BUBBLE);
            //Unlocking wave grants the ability to push and pull
            if (wave != null && (data.canUse(wave) || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()))
                data.addStatusControl(StatusControlController.PUSH_SHIELD_BUBBLE);
        }

        return true;
    }
}
