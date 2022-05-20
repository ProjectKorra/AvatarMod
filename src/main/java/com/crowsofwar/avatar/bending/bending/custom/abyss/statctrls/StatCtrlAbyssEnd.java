package com.crowsofwar.avatar.bending.bending.custom.abyss.statctrls;

import com.crowsofwar.avatar.bending.bending.custom.abyss.Abyssbending;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

import static com.crowsofwar.avatar.bending.bending.custom.abyss.tickhandlers.AbyssChargeHandler.ABYSS_END_MOVE_MOD_ID;
import static com.crowsofwar.avatar.util.data.TickHandlerController.ABYSS_END_CHARGER;
import static com.crowsofwar.avatar.util.data.TickHandlerController.ABYSS_END_RAIN;

public class StatCtrlAbyssEnd extends StatusControl {

    private final boolean setting;

    public StatCtrlAbyssEnd(boolean setting) {
        super(setting ? 11 : 12, setting ? AvatarControl.CONTROL_RIGHT_CLICK_DOWN : AvatarControl.CONTROL_RIGHT_CLICK_UP,
                CrosshairPosition.RIGHT_OF_CROSSHAIR);
        this.setting = setting;
    }

    @Override
    public boolean execute(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase bender = ctx.getBenderEntity();

        if (data.hasBendingId(Abyssbending.ID)) {
            if (setting) {
                data.addStatusControl(StatusControlController.RELEASE_ABYSS_END);
                data.addTickHandler(ABYSS_END_CHARGER, ctx);
            } else {
                if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ABYSS_END_MOVE_MOD_ID) != null)
                    bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ABYSS_END_MOVE_MOD_ID);
                data.removeTickHandler(ABYSS_END_CHARGER, ctx);
                data.removeTickHandler(ABYSS_END_RAIN, ctx);
            }
        }

        return true;
    }
}
