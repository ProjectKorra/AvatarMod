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
package com.crowsofwar.avatar.common.gui;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Causes the player to open the sky bison inventory instead of their own while
 * riding a bison.
 * 
 * @author CrowsOfWar
 */
public class AvatarInventoryOverride {
	
	private AvatarInventoryOverride() {}
	
	@SubscribeEvent
	public void onInventoryOpen(PlayerContainerEvent.Open e) {
		if (e.getContainer() instanceof ContainerPlayer) {
			System.out.println("Override inventory");
			EntityPlayer player = e.getEntityPlayer();
			if (player.getRidingEntity() instanceof EntitySkyBison) {
				
			}
		}
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AvatarInventoryOverride());
	}
	
}
