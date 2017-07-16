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

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class FallAbsorptionHandler {
	
	private FallAbsorptionHandler() {}
	
	@SubscribeEvent
	public void onFall(LivingFallEvent e) {
		Entity entity = e.getEntity();
		if (entity instanceof EntityPlayer && !entity.world.isRemote) {
			EntityPlayer player = (EntityPlayer) entity;
			BendingData data = BendingData.get(player);
			if (data.getFallAbsorption() != 0) {
				e.setDistance(e.getDistance() - data.getFallAbsorption());
				if (e.getDistance() < 0) e.setDistance(0);
				data.setFallAbsorption(0);
			}
		}
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new FallAbsorptionHandler());
	}
	
}
