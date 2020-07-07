package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

public class AvatarTriggers {
    public static final UnlockBendingTrigger UNLOCK_ELEMENT_AIR = new UnlockBendingTrigger("airbending/unlock_element_air");
    public static final UnlockBendingTrigger UNLOCK_ELEMENT_WATER = new UnlockBendingTrigger("waterbending/unlock_element_water");
    public static final UnlockBendingTrigger UNLOCK_ELEMENT_EARTH = new UnlockBendingTrigger("earthbending/unlock_element_earth");
    public static final UnlockBendingTrigger UNLOCK_ELEMENT_FIRE = new UnlockBendingTrigger("firebending/unlock_element_fire");

    /*
     * This array just makes it convenient to register all the criteria.
     */
    public static final ICriterionTrigger[] TRIGGER_ARRAY = new ICriterionTrigger[] {
            UNLOCK_ELEMENT_AIR,
            UNLOCK_ELEMENT_WATER,
            UNLOCK_ELEMENT_EARTH,
            UNLOCK_ELEMENT_FIRE,
    };

    //Helpers

    public static void TriggerUnlockElement(BendingStyle element, EntityLivingBase entity) {
        AvatarLog.info("entering method:" + element.getName());
        if(element.getName() == "airbending")
            UNLOCK_ELEMENT_AIR.trigger((EntityPlayerMP) entity);
        if(element.getName() == "waterbending")
            UNLOCK_ELEMENT_WATER.trigger((EntityPlayerMP) entity);
        if(element.getName() == "earthbending")
            UNLOCK_ELEMENT_EARTH.trigger((EntityPlayerMP) entity);
        if(element.getName() == "firebending")
            UNLOCK_ELEMENT_FIRE.trigger((EntityPlayerMP) entity);

    }
}
