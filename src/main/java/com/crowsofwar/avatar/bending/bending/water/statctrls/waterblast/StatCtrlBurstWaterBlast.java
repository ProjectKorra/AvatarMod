package com.crowsofwar.avatar.bending.bending.water.statctrls.waterblast;

import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

import static com.crowsofwar.avatar.bending.bending.water.tickhandlers.WaterChargeHandler.WATER_CHARGE_MOVEMENT_ID;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_WATER;
import static com.crowsofwar.avatar.util.data.TickHandlerController.WATER_CHARGE;
import static com.crowsofwar.avatar.util.data.TickHandlerController.WATER_PARTICLE_SPAWNER;

public class StatCtrlBurstWaterBlast extends StatusControl {

    public StatCtrlBurstWaterBlast() {
        super(26, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase bender = ctx.getBenderEntity();

        if (data.hasBendingId(Waterbending.ID)) {

        }
        return true;
    }
}
