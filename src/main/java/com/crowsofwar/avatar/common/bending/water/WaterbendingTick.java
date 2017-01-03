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
package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class WaterbendingTick {
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		EntityPlayer player = e.player;
		World world = player.worldObj;
		if (!world.isRemote) {
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
			
			tryStartSkating(data, player);
		}
	}
	
	private void tryStartSkating(AvatarPlayerData data, EntityPlayer player) {
		if (data.getSkateTime() > 0) {
			data.setSkateTime(data.getSkateTime() - 1);
			if (player.isInWater()) {
				data.setSkateTime(0);
				data.setSkating(true);
			}
		}
	}
	
}
