package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

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
