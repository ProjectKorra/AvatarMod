package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.util.event.AbilityLevelEvent;
import com.crowsofwar.avatar.util.event.AbilityUnlockEvent;
import com.crowsofwar.avatar.util.event.ElementUnlockEvent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.crowsofwar.avatar.util.event.AbilityUseEvent;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarTriggers {
    /*
    UnlockBendingTrigger is working, LevelAbilityTrigger is working.
     */
    public static final UnlockBendingTrigger UNLOCK_ELEMENT = new UnlockBendingTrigger("unlock_bending");
    public static final LevelAbilityTrigger ABILITY_LEVEL = new LevelAbilityTrigger("level_ability");
    public static final UseAbilityTrigger ABILITY_USE = new UseAbilityTrigger("use_ability");
    public static final ElementRankupTrigger ELEMENT_RANK = new ElementRankupTrigger("rank_element");
    public static final AbilityUnlockTrigger UNLOCK_ABILITY = new AbilityUnlockTrigger("ability_unlock");
    //TODO: [AD] Discuss how this one should be done, is there a total levels per element we can hook into?
    public static final ICriterionTrigger ELEMENT_RANKUP = new UnlockBendingTrigger("");

    public static void init() {
        CriteriaTriggers.register(UNLOCK_ELEMENT);
        CriteriaTriggers.register(ABILITY_LEVEL);
        CriteriaTriggers.register(ABILITY_USE);
        CriteriaTriggers.register(ELEMENT_RANK);
    }

    @SubscribeEvent
    public void unlockBending(ElementUnlockEvent event){
        AvatarTriggers.UNLOCK_ELEMENT.trigger((EntityPlayerMP)event.getEntity(), event.getElement());
    }
    @SubscribeEvent
    public void levelAbility(AbilityLevelEvent event){
        AvatarTriggers.ABILITY_LEVEL.trigger((EntityPlayerMP)event.getEntity(), event.getAbility(), event.getOldLevel(), event.getNewLevel());
        AvatarTriggers.ELEMENT_RANK.trigger((EntityPlayerMP)event.getEntity(), event.getAbility().getElement(), event.getOldLevel(), event.getNewLevel());
    }
    @SubscribeEvent
    public void useAbility(AbilityUseEvent event){
        AvatarTriggers.ABILITY_USE.trigger((EntityPlayerMP)event.getEntity(), event.getAbility(), event.getLevel());
    }
    @SubscribeEvent
    public void unlockAbility(AbilityUnlockEvent event) {
        AvatarTriggers.UNLOCK_ABILITY.trigger((EntityPlayerMP) event.getEntity(), event.getAbility());
        //AvatarTriggers.ELEMENT_RANK.trigger((EntityPlayerMP)event.getEntity(), event.getAbility().getElement());
    }
}