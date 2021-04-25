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

public class StatCtrlChargeWaterBlast extends StatusControl {

    boolean setting;

    public StatCtrlChargeWaterBlast(boolean setting) {
        super(setting ? 26 : 25, setting ? AvatarControl.CONTROL_RIGHT_CLICK_DOWN : AvatarControl.CONTROL_RIGHT_CLICK_UP, CrosshairPosition.RIGHT_OF_CROSSHAIR);
        this.setting = setting;

    }

    @Override
    public boolean execute(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase bender = ctx.getBenderEntity();

        if (data.hasBendingId(Waterbending.ID)) {
            if (setting) {
                data.addStatusControl(RELEASE_WATER);
                data.addTickHandler(WATER_PARTICLE_SPAWNER, ctx);
                data.addTickHandler(WATER_CHARGE, ctx);
            } else {
                if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(WATER_CHARGE_MOVEMENT_ID) != null)
                    bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(WATER_CHARGE_MOVEMENT_ID);
                data.removeStatusControl(RELEASE_WATER);
                //We don't remove the status control here since we want to spawn the tick handler if we stop right clicking.
            }
        }
        return true;
    }
}
