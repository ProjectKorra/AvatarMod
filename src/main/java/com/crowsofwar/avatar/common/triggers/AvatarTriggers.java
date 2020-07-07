package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

public class AvatarTriggers {
    public static final UnlockBendingTrigger UNLOCK_ELEMENT = new UnlockBendingTrigger("unlock_bending");
    public static final ICriterionTrigger ABILITY_USE = new UnlockBendingTrigger("");
    public static final ICriterionTrigger ABILITY_LEVEL = new UnlockBendingTrigger("");
    public static final UnlockBendingTrigger ELEMENT_RANKUP = new UnlockBendingTrigger("");

    /*
     * This array just makes it convenient to register all the criteria.
     */
    public static final ICriterionTrigger[] TRIGGER_ARRAY = new ICriterionTrigger[] {
            UNLOCK_ELEMENT,
    };

    public static void init() {
        CriteriaTriggers.register(UNLOCK_ELEMENT);
    }
}
