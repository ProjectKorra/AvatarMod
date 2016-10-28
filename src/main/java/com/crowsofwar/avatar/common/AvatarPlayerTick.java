package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class AvatarPlayerTick {
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		if (e.side == Side.CLIENT) AvatarPlayerData.fetcher().fetchPerformance(e.player);
	}
	
}
