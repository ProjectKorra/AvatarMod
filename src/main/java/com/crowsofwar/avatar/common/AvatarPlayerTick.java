package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public class AvatarPlayerTick {
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		if (e.side == Side.SERVER || true) {
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(e.player);
			if (data != null) {
				if (data.getActiveBendingController() != null) {
					data.getState().update(e.player, null);
					data.getActiveBendingController().onUpdate(data);
				}
			}
		}
	}
	
}
