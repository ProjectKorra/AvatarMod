package com.maxandnoah.avatar.common;

import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.data.AvatarPlayerDataFetcherServer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public class AvatarPlayerTick {
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		if (e.side == Side.SERVER || true) {
			AvatarPlayerData data = AvatarPlayerDataFetcherServer.instance.getDataPerformance(e.player);
			if (data != null) {
				if (data.getActiveBendingController() != null) {
					data.getState().update(e.player, null);
					data.getActiveBendingController().onUpdate(data);
				}
			}
		}
	}
	
}
