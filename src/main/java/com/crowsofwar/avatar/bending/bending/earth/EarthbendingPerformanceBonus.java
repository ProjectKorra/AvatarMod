package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.util.data.BendingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class EarthbendingPerformanceBonus {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {

        if (e.phase == TickEvent.Phase.START && e.side == Side.SERVER) {

            EntityPlayer player = e.player;
            BendingData data = BendingData.getFromEntity(player);

            if (data != null) {
                BattlePerformanceScore performance = data.getPerformance();
                double score = performance.getScore();

                if (score >= 80) {
                    player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 30, 0));
                    performance.modifyScore(-20);
                }
                if (score >= 100) {
                    player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 60, 1));
                    performance.modifyScore(-60);
                }
            }
        }

    }

}
