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
package com.crowsofwar.avatar.common;

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.ctx.Bender;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class SleepChiRegenHandler {
	
	private SleepChiRegenHandler() {}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new SleepChiRegenHandler());
	}
	
	@SubscribeEvent
	public void onSlept(PlayerWakeUpEvent e) {
		EntityPlayer player = e.getEntityPlayer();
		BendingData data = Bender.getData(player);
		Chi chi = data.chi();
		
		chi.setAvailableChi(chi.getMaxChi() * CHI_CONFIG.availableThreshold);
		chi.changeTotalChi(STATS_CONFIG.sleepChiRegen);
	}
	
}
