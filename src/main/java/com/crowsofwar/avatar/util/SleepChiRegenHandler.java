/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.util.analytics.AnalyticEvents;
import com.crowsofwar.avatar.util.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.Chi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.crowsofwar.avatar.config.ConfigChi.CHI_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class SleepChiRegenHandler {

	@SubscribeEvent
	public static void onSlept(PlayerWakeUpEvent e) {
		EntityPlayer player = e.getEntityPlayer();
		BendingData data = BendingData.getFromEntity(player);
		if (data != null) {
			Chi chi = data.chi();
			World world = player.world;

			//Checks on sleep
			if (world.getWorldTime() % 24000 <= 2) {
				chi.setAvailableChi(CHI_CONFIG.maxAvailableChi);
				chi.changeTotalChi(STATS_CONFIG.sleepChiRegen);
				AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onSleepRestoration());
			}
		}

	}

}
