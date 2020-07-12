package com.crowsofwar.avatar.common.triggers;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;

public class AvatarTriggers {
    public static final UnlockBendingTrigger UNLOCK_ELEMENT = new UnlockBendingTrigger("unlock_bending");
    public static final LevelAbilityTrigger ABILITY_LEVEL = new LevelAbilityTrigger("level_ability");
    public static final UseAbilityTrigger ABILITY_USE = new UseAbilityTrigger("use_ability");
    //TODO: [AD] Discuss how this one should be done, is there a total levels per element we can hook into?
    public static final ICriterionTrigger ELEMENT_RANKUP = new UnlockBendingTrigger("");

    public static void init() {
        CriteriaTriggers.register(UNLOCK_ELEMENT);
        CriteriaTriggers.register(ABILITY_LEVEL);
        CriteriaTriggers.register(ABILITY_USE);
    }
}
