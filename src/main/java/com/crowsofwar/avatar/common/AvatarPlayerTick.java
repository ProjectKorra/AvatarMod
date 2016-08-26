package com.crowsofwar.avatar.common;

import java.util.List;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class AvatarPlayerTick {
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(e.player);
		if (data != null) {
			BendingController controller = data.getActiveBendingController();
			if (controller != null) {
				data.getState().update(e.player, null);
				List<BendingAbility> abilities = controller.getAllAbilities();
				for (BendingAbility ability : abilities) {
					if (ability.requiresUpdateTick()) {
						ability.update(data);
					}
				}
			}
		}
	}
	
}
