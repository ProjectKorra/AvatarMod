package com.crowsofwar.avatar.bending.bending.fire;

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

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FirebendingPerformanceHealthRegen {

	@SubscribeEvent
	public static void onFirebenderUpdate(TickEvent.PlayerTickEvent e) {

		if (e.phase == TickEvent.Phase.START) {

			EntityPlayer player = e.player;
			World world = player.world;
			BendingData data = BendingData.get(player);

			if (!world.isRemote && data.hasBendingId(Firebending.ID)) {

				BattlePerformanceScore performance = data.getPerformance();
				double score = performance.getScore();

				// Provide health regen/benefits
				if (score == 100) {
					performance.modifyScore(-31);
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, 1));
				} else if (score >= 70 && score < 100) {
					// Heals 1/2 heart
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, 0));
					performance.modifyScore(-20);
				}

			}

		}

	}

}
