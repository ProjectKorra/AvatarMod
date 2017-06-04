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

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.crowsofwar.avatar.common.entity.mob.EntityFirebender;
import com.crowsofwar.avatar.common.entity.mob.EntityHumanBender;

import net.minecraft.entity.passive.EntityVillager;
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
		MinecraftForge.TERRAIN_GEN_BUS.register(new HumanBenderSpawner());
	}
	
	@SubscribeEvent
	public void modifyVillageSpawner(InitMapGenEvent e) {
		
		System.out.println("aaabbb");
		
		System.out.println(e.getType());
		if (e.getType() == EventType.VILLAGE) {
			// TODO See if this messes up superflat world options
			System.out.println("Modify village spawner");
			e.setNewGen(new MapGenVillageWithHumanbenders());
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
			if (result) {
				
				// This list contains villagers in that structure
				
				List<EntityVillager> villagers = worldIn.getEntities(EntityVillager.class, villager -> {
					return new ChunkPos(villager.getPosition()).equals(chunkCoord);
				});
				
				double chance = 40;
				Random rand = new Random();
				if (!villagers.isEmpty() && rand.nextDouble() * 100 < chance) {
					EntityHumanBender bender = new EntityFirebender(worldIn);
					bender.copyLocationAndAnglesFrom(villagers.get(0));
					worldIn.spawnEntityInWorld(bender);
				}
				
			}
			return result;
		}
		
	}
	
}
