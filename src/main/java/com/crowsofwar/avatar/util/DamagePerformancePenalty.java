package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.util.data.BendingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Applies a performance penalty when players take damage.
 *
 * @see BattlePerformanceScore
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class DamagePerformancePenalty {

    @SubscribeEvent
    public static void onTakenDamage(LivingHurtEvent e) {

        EntityLivingBase entity = e.getEntityLiving();

        if (entity instanceof EntityPlayer && !entity.world.isRemote) {

            float penalty = e.getAmount() * -5;

            BendingData data = BendingData.getFromEntity(entity);
            if (data != null)
                data.getPerformance().modifyScore(penalty);

        }

    }

}
