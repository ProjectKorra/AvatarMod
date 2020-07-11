package com.crowsofwar.avatar.bending.bending.water;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.util.data.BendingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class WaterbendingPerformanceBonus {

	@SubscribeEvent
	public static void onWaterbenderUpdate(TickEvent.PlayerTickEvent e) {
		if (e.phase == TickEvent.Phase.START) {
			EntityPlayer player = e.player;
			World world = player.world;
			BendingData data = BendingData.getFromEntity(player);

			if (!world.isRemote && data != null && data.hasBendingId(Waterbending.ID)) {

				BattlePerformanceScore performance = data.getPerformance();
				double score = data.getPerformance().getScore();

				if (score == 100) {
					player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 80, 1));
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 80, 0));
					performance.setScore(0);
				} else if (score >= 80) {
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, 1));
					performance.setScore(0);
				}

			}
		}
	}

}
