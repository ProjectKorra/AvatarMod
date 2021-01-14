package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.event.AbilityLevelEvent;
import com.crowsofwar.avatar.util.event.AbilityUnlockEvent;
import com.crowsofwar.avatar.util.event.AbilityUseEvent;
import com.crowsofwar.avatar.util.event.ElementUnlockEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarTriggers {
    //TODO: Write autogeneration for all of the advancements for levelling up.
    // Pain.
    public static final UnlockBendingTrigger UNLOCK_ELEMENT = new UnlockBendingTrigger("unlock_bending");
    public static final LevelAbilityTrigger ABILITY_LEVEL = new LevelAbilityTrigger("level_ability");
    public static final UseAbilityTrigger ABILITY_USE = new UseAbilityTrigger("use_ability");
    public static final ElementRankupTrigger ELEMENT_RANK = new ElementRankupTrigger("rank_element");
    public static final AbilityUnlockTrigger UNLOCK_ABILITY = new AbilityUnlockTrigger("unlock_ability");

    public static void init() {
        CriteriaTriggers.register(UNLOCK_ELEMENT);
        CriteriaTriggers.register(ABILITY_LEVEL);
        CriteriaTriggers.register(ABILITY_USE);
        CriteriaTriggers.register(ELEMENT_RANK);
        CriteriaTriggers.register(UNLOCK_ABILITY);
    }

    @SubscribeEvent
    public static void unlockBending(ElementUnlockEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP)
            AvatarTriggers.UNLOCK_ELEMENT.trigger((EntityPlayerMP) event.getEntity(), event.getElement());
    }

    @SubscribeEvent
    public static void levelAbility(AbilityLevelEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            AvatarTriggers.ABILITY_LEVEL.trigger((EntityPlayerMP) event.getEntity(), event.getAbility(), event.getOldLevel(), event.getNewLevel());
            //Time to calculate the player's rank ugh
            BendingData data = BendingData.getFromEntity(event.getEntityLiving());
            if (data != null) {
                Ability ability = event.getAbility();

                BendingData.Rank rank = data.getRank(ability.getElement(), 0);
                BendingData.Rank newRank = data.getRank(ability.getElement(), 1);
                AvatarTriggers.ELEMENT_RANK.trigger((EntityPlayerMP) event.getEntity(), event.getAbility().getElement(), rank.ordinal(), newRank.ordinal());
            }
        }
    }

    @SubscribeEvent
    public static void useAbility(AbilityUseEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP)
            AvatarTriggers.ABILITY_USE.trigger((EntityPlayerMP) event.getEntity(), event.getAbility(), event.getLevel() - 1);
    }

    @SubscribeEvent
    public static void unlockAbility(AbilityUnlockEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP)
            AvatarTriggers.UNLOCK_ABILITY.trigger((EntityPlayerMP) event.getEntity(), event.getAbility());
    }
}