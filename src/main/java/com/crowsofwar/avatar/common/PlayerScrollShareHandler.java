package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.analytics.AnalyticEvent;
import com.crowsofwar.avatar.common.analytics.AnalyticEvents;
import com.crowsofwar.avatar.common.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.scroll.ItemScroll;
import com.crowsofwar.avatar.common.item.scroll.Scrolls;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles analytic for players giving scrolls to each other.
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class PlayerScrollShareHandler {

	@SubscribeEvent
	public static void onPickupItem(EntityItemPickupEvent e) {

		EntityItem entity = e.getItem();
		String throwingPlayer = entity.getThrower();
		String pickupPlayer = e.getEntityPlayer().getName();

		// Thrower can actually be null, despite compiler's predictions
		//noinspection ConstantConditions
		if (throwingPlayer != null && !throwingPlayer.equals(pickupPlayer)) {

			// Two players just shared an item
			// If it's a scroll, send the scroll shared statistic

			ItemStack stack = entity.getItem();

			if (stack.getItem() instanceof ItemScroll) {

				String type = Scrolls.getTypeForStack(stack).name().toLowerCase();
				AnalyticEvent analyticEvent = AnalyticEvents.onScrollShared(type);
				AvatarAnalytics.INSTANCE.pushEvent(analyticEvent);

			}

		}

	}

}
