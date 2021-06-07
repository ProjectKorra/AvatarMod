package com.crowsofwar.avatar.bending.bending.water.statctrls.waterblast;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import static com.crowsofwar.avatar.bending.bending.water.tickhandlers.WaterChargeHandler.WATER_CHARGE_MOVEMENT_ID;
import static com.crowsofwar.avatar.util.data.StatusControlController.BURST_WATER;
import static com.crowsofwar.avatar.util.data.TickHandlerController.WATER_BURST;
import static com.crowsofwar.avatar.util.data.TickHandlerController.WATER_CHARGE;

public class StatCtrlBurstWaterBlast extends StatusControl {

    public StatCtrlBurstWaterBlast() {
        super(26, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        //Uses a tickhandler due to pulling
        BendingData data = ctx.getData();
        if (ctx.getData().hasTickHandler(WATER_CHARGE)) {
            //data.addTickHandler(TickHandlerController.SHOOT_AIRBURST, ctx);
            AttributeModifier mod = ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                    .getModifier(WATER_CHARGE_MOVEMENT_ID);
            if (mod != null) {
                ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(mod);
            }
        }
        data.addTickHandler(WATER_BURST, ctx);
        data.removeTickHandler(WATER_CHARGE, ctx);
        data.removeStatusControl(BURST_WATER);
        return true;
    }

}
