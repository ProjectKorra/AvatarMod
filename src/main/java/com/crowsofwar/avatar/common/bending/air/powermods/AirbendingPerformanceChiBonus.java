package com.crowsofwar.avatar.common.bending.air.powermods;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;

/**
 * Grants airbenders a bonus where {@link com.crowsofwar.avatar.common.bending.BattlePerformanceScore doing well in combat}
 * grants them faster chi regeneration.
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AirbendingPerformanceChiBonus {

	@SubscribeEvent
	public static void onAirbenderUpdate(TickEvent.PlayerTickEvent e) {

		EntityPlayer player = e.player;
		World world = player.world;

		if (!world.isRemote) {

			Bender bender = Bender.get(player);
			BendingData data = bender.getData();

			if (data.hasBendingId(Airbending.ID) && !player.isCreative()) {

				double performanceScore = data.getPerformance().getScore();

				if (performanceScore > 20) {

					// Grant chi boost
					double boostPct = getChiMultiplier(performanceScore) - 1;
					float availableChi = (float) (CHI_CONFIG.availablePerSecond / 20f * boostPct);
					float totalChi = (float) (CHI_CONFIG.regenPerSecond / 20f * boostPct / 2);

					Chi chi = data.chi();
					if (chi.getAvailableChi() < chi.getAvailableMaxChi()) {
						chi.changeAvailableChi(availableChi);
						chi.changeTotalChi(totalChi);
					}

				}

			}

		}

	}

	/**
	 * Gets the multiplier of how more fast chi should regenerate per second - for example, a value
	 * of 1.2 means chi regenerates 20% faster. Here, the AirbendingPerformanceChiBonus will simply
	 * add 20% more the chi regeneration, which is in addition to the amount already being added by
	 * Bender#onUpdate.
	 */
	private static double getChiMultiplier(double performance) {
		if (performance > 0) {
			return 1 + performance / 150;
		} else {
			return 1 + performance / 300;
		}
	}

}
