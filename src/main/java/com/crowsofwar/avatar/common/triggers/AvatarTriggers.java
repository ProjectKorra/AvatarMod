package com.crowsofwar.avatar.common.triggers;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;

public class AvatarTriggers {
    public static final ICriterionTrigger UNLOCK_ELEMENT = new UnlockBendingTrigger("unlock_bending");
    public static final ICriterionTrigger ABILITY_USE = new UnlockAbilityTrigger("unlock_ability");
    public static final ICriterionTrigger ABILITY_LEVEL = new UnlockBendingTrigger("");
    public static final ICriterionTrigger ELEMENT_RANKUP = new UnlockBendingTrigger("");

    public static void init() {
        CriteriaTriggers.register(UNLOCK_ELEMENT);
        CriteriaTriggers.register(ABILITY_USE);
    }
}
