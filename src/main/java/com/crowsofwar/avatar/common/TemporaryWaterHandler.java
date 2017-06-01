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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 * A hacky workaround to manage a temporary water block at a position. The water
 * block will flow outwards. Creating a flowing water block (which would
 * naturally disappear) still wouldn't work since it wouldn't flow outwards.
 * 
 * @author CrowsOfWar
 */
public class TemporaryWaterHandler {
	
	private TemporaryWaterHandler() {}
	
	@SubscribeEvent
	public void onTick(ServerTickEvent e) {
		if (e.phase == Phase.START) {
			
		}
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new TemporaryWaterHandler());
	}
	
}
