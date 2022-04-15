package com.crowsofwar.avatar.bending.bending.custom.hyper.statctrls;

import com.crowsofwar.avatar.bending.bending.custom.hyper.Hyperbending;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

import static com.crowsofwar.avatar.bending.bending.custom.hyper.tickhandlers.AdventChargeHandler.ADVENT_MOVE_MOD_ID;
import static com.crowsofwar.avatar.util.data.TickHandlerController.*;

public class StatCtrlHyperAdvent extends StatusControl {

    private boolean setting;

    public StatCtrlHyperAdvent(boolean setting) {
        super(setting ? 11 : 12, setting ? AvatarControl.CONTROL_RIGHT_CLICK_DOWN : AvatarControl.CONTROL_RIGHT_CLICK_UP,
                CrosshairPosition.RIGHT_OF_CROSSHAIR);
        this.setting = setting;
    }

    @Override
    public boolean execute(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase bender = ctx.getBenderEntity();

        if (data.hasBendingId(Hyperbending.ID)) {
            if (setting) {
                data.addStatusControl(StatusControlController.RELEASE_HYPER_ADVENT);
                data.addTickHandler(HYPER_ADVENT_CHARGER, ctx);
            } else {
                if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ADVENT_MOVE_MOD_ID) != null)
                    bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ADVENT_MOVE_MOD_ID);
                data.removeTickHandler(HYPER_ADVENT_CHARGER, ctx);
                data.removeTickHandler(HYPER_ADVENT_RAIN, ctx);
             }
        }

        return true;
    }
}
