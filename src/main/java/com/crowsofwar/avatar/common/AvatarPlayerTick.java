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

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarPlayerTick {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent e) {
		// Also forces loading of data on client
		Bender bender = Bender.get(e.player);
		if (bender != null) {
			BendingData data = bender.getData();

			EntityPlayer player = e.player;

			if (!player.world.isRemote && player.ticksExisted == 0) {
				data.saveAll();
			}

			if (e.phase == Phase.START) {
				bender.onUpdate();
			}
			
		}
		
	}
	
}
