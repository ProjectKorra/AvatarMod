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

import java.util.List;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.Raytrace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class AvatarPlayerTick {
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		// Also forces loading of data on client
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(e.player);
		if (data != null) {
			
			EntityPlayer player = e.player;
			
			if (!player.worldObj.isRemote && player.ticksExisted == 0) {
				data.saveAll();
			}
			
			data.decrementCooldown();
			if (!player.worldObj.isRemote) {
				Chi chi = data.chi();
				chi.changeTotalChi(CHI_CONFIG.regenPerSecond / 20f);
				
				if (chi.getAvailableChi() < chi.getMaxChi() * CHI_CONFIG.availableThreshold) {
					chi.changeAvailableChi(CHI_CONFIG.availablePerSecond / 20f);
				}
				
			}
			
			if (e.phase == Phase.START) {
				List<TickHandler> tickHandlers = data.getAllTickHandlers();
				if (tickHandlers != null) {
					BendingContext ctx = new BendingContext(data, new Raytrace.Result());
					for (TickHandler handler : tickHandlers) {
						if (handler.tick(ctx)) {
							// Can use this since the list is a COPY of the
							// underlying list
							data.removeTickHandler(handler);
						}
					}
				}
			}
			
		}
		
	}
	
}
