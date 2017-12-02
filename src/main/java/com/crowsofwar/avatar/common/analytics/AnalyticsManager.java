package com.crowsofwar.avatar.common.analytics;

import com.crowsofwar.avatar.AvatarInfo;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Manages the relation between analytics and the game; sends analytics at the right time to do so.
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AnalyticsManager {

	/**
	 * Last time since data was sent
	 */
	private static long lastSendTime = System.currentTimeMillis();

	@SubscribeEvent
	public static void onTick(TickEvent e) {

		if (e.phase != TickEvent.Phase.START) {
			return;
		}
		if (e instanceof TickEvent.ClientTickEvent || e instanceof TickEvent.ServerTickEvent) {

			int unsent = AvatarAnalytics.INSTANCE.getUnsentEventsAmount();

			if (unsent >= 10) {
				uploadEvents();
			}

			if (unsent > 0 && System.currentTimeMillis() - lastSendTime > 20 * 1000) {
				uploadEvents();
			}

		}

	}

	@SubscribeEvent
	public static void onShutdown(WorldEvent.Unload e) {
		uploadEvents();
	}

	private static void uploadEvents() {
		lastSendTime = System.currentTimeMillis();
		AvatarAnalytics.INSTANCE.uploadEvents();
		System.out.println("uploaded");
	}

}
