package com.crowsofwar.avatar.common;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.analytics.*;
import com.crowsofwar.avatar.common.item.*;

/**
 * Handles analytic for players giving scrolls to each other.
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MODID)
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

			if (stack.getItem() == AvatarItems.itemScroll) {

				String type = ItemScroll.ScrollType.get(stack.getMetadata()).name().toLowerCase();
				AnalyticEvent analyticEvent = AnalyticEvents.onScrollShared(type);
				AvatarAnalytics.INSTANCE.pushEvent(analyticEvent);

			}

		}

	}

}
