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
package com.crowsofwar.avatar.common.bending.earth;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EarthbendingEvents {
	
	private EarthbendingEvents() {}
	
	@SubscribeEvent
	public void digSpeed(PlayerEvent.BreakSpeed e) {
		EntityPlayer player = e.getEntityPlayer();
		World world = player.worldObj;
		
		IBlockState state = e.getState();
		if (STATS_CONFIG.bendableBlocks.contains(state.getBlock())) {
			e.setNewSpeed(e.getOriginalSpeed() * 2);
		}
		
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new EarthbendingEvents());
	}
	
}
