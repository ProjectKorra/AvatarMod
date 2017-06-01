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

import java.util.Iterator;
import java.util.List;

import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.avatar.common.data.TemporaryWaterLocation;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;

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
	public void onTick(WorldTickEvent e) {
		if (e.phase == Phase.START && e.side == Side.SERVER) {
			
			World world = e.world;
			System.out.println(world.provider.getDimension());
			AvatarWorldData wd = AvatarWorldData.getDataFromWorld(world);
			
			List<TemporaryWaterLocation> twls = wd.geTemporaryWaterLocations();
			Iterator<TemporaryWaterLocation> iterator = twls.iterator();
			
			while (iterator.hasNext()) {
				
				TemporaryWaterLocation twl = iterator.next();
				
				System.out.println("twl @" + twl.getPos());
				twl.decrementTicks();
				if (twl.getTicks() <= 0) {
					BlockPos pos = twl.getPos();
					if (world.getBlockState(pos).getBlock() == Blocks.WATER) {
						// world.setBlockToAir(pos);
						world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						System.out.println("REMOVE");
					}
					
					iterator.remove();
				}
				
			}
			
		}
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new TemporaryWaterHandler());
	}
	
}
