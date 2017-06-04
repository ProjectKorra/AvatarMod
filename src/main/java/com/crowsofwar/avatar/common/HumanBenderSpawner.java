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

import java.util.Map;
import java.util.Random;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class HumanBenderSpawner {
	
	private HumanBenderSpawner() {}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new HumanBenderSpawner());
	}
	
	@SubscribeEvent
	public void modifyVillageSpawner(InitMapGenEvent e) {
		
		System.out.println(e.getType());
		if (e.getType() == EventType.VILLAGE) {
			System.out.println("Modify village spawner");
			
		}
		
	}
	
	private static class MapGenVillageWithHumanbenders extends MapGenVillage {
		
		public MapGenVillageWithHumanbenders() {
			super();
		}
		
		public MapGenVillageWithHumanbenders(Map<String, String> map) {
			super(map);
		}
		
		@Override
		public synchronized boolean generateStructure(World worldIn, Random randomIn, ChunkPos chunkCoord) {
			boolean result = super.generateStructure(worldIn, randomIn, chunkCoord);
			System.out.println("Result: " + result);
			if (result) {
				System.out.println("Proceed to spawn some humanbenders");
			}
			return result;
		}
		
	}
	
}
