package com.crowsofwar.avatar.bending.bending.custom.demonic.statctrls;

import com.crowsofwar.avatar.bending.bending.custom.dark.Darkbending;
import com.crowsofwar.avatar.bending.bending.custom.demonic.Demonbending;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.EnumHand;

import static com.crowsofwar.avatar.bending.bending.custom.dark.tickhandlers.ShadeBurstHandler.SHADE_BURST_MOVEMENT_MOD_ID;
import static com.crowsofwar.avatar.bending.bending.custom.demonic.tickhandlers.InfernalFieldHandler.INFERNAL_FIELD_MOVEMENT_MOD_ID;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_UP;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_INFERNAL_FIELD;
import static com.crowsofwar.avatar.util.data.StatusControlController.SHOOT_SHADE_BURST;
import static com.crowsofwar.avatar.util.data.TickHandlerController.INFERNAL_FIELD_HANDLER;
import static com.crowsofwar.avatar.util.data.TickHandlerController.SHADE_BURST_CHARGE;

public class StatCtrlInfernalField extends StatusControl {

    private final boolean setting;

    public StatCtrlInfernalField(boolean setting) {
        super(setting ? 11 : 12, setting ? CONTROL_RIGHT_CLICK_DOWN : CONTROL_RIGHT_CLICK_UP,
                RIGHT_OF_CROSSHAIR);
        this.setting = setting;
    }

    @Override
    public boolean execute(BendingContext ctx) {

        BendingData data = ctx.getData();
        EntityLivingBase bender = ctx.getBenderEntity();

        if (data.hasBendingId(Demonbending.ID)) {
            if (setting) {
                data.addStatusControl(RELEASE_INFERNAL_FIELD);
                data.addTickHandler(INFERNAL_FIELD_HANDLER, ctx);
            } else {
                if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(INFERNAL_FIELD_MOVEMENT_MOD_ID) != null)
                    bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(INFERNAL_FIELD_MOVEMENT_MOD_ID);
                //We don't remove the status control here since we want to spawn the tick handler if we stop right clicking.
                bender.swingArm(EnumHand.MAIN_HAND);
            }
        }

        return true;
    }
}
