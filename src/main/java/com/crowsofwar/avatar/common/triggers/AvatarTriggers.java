package com.crowsofwar.avatar.common.triggers;

import net.minecraft.advancements.ICriterionTrigger;

public class AvatarTriggers {
    public static final UnlockBendingTrigger UNLOCK_AN_ELEMENT = new UnlockBendingTrigger("unlock_an_element");

    /*
     * This array just makes it convenient to register all the criteria.
     */
    public static final ICriterionTrigger[] TRIGGER_ARRAY = new UnlockBendingTrigger[] {
            UNLOCK_AN_ELEMENT
    };
}
