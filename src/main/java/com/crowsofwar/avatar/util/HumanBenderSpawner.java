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
package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.entity.mob.EntityAirbender;
import com.crowsofwar.avatar.entity.mob.EntityFirebender;
import com.crowsofwar.avatar.entity.mob.EntityHumanBender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.crowsofwar.avatar.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class HumanBenderSpawner {

	public static void register() {
		MinecraftForge.TERRAIN_GEN_BUS.register(new HumanBenderSpawner());
	}

	//Doesn't work, dunno why
	@SubscribeEvent
	public void modifyVillageSpawner(InitMapGenEvent e) {

		if (e.getType() == EventType.VILLAGE) {
			// TODO See if this messes up superflat world options
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
				//TODO: More immersive spawning


				// This list contains villagers in that structure
				List<EntityVillager> villagers = worldIn.getEntities(EntityVillager.class, villager -> {
					assert villager != null;
					return new ChunkPos(villager.getPosition()).equals(chunkCoord);
				});
				if (!villagers.isEmpty()) {


					// To attempt to have all humanbenders be same type, check if
					// there are nearby humanbenders
					// If there are just copy their type
					AxisAlignedBB aabb = new AxisAlignedBB(villagers.get(0).posX + 100, villagers.get(0).posY + 100, villagers.get(0).posZ + 100,
							villagers.get(0).posX - 100, villagers.get(0).posY - 100, villagers.get(0).posZ - 100);
					List<EntityHumanBender> nearbyBenders = worldIn.getEntitiesWithinAABB(EntityHumanBender.class,
							aabb);

					Random rand = new Random();
					boolean firebender;

					if (nearbyBenders.isEmpty()) {
						firebender = rand.nextBoolean();
					} else {
						firebender = !(nearbyBenders.get(0) instanceof EntityFirebender);
					}

					for (Entity e : villagers) {
						int i = AvatarUtils.getRandomNumberInRange(1, 2);
						if (i == 2) {
							EntityHumanBender bender = firebender ? new EntityFirebender(worldIn)
									: new EntityAirbender(worldIn);
							bender.copyLocationAndAnglesFrom(e);
							bender.setLevel(AvatarUtils.getRandomNumberInRange(1, MOBS_CONFIG.benderSettings.maxLevel));
							bender.setHomePosAndDistance(e.getPosition(), 20);
							if (!worldIn.isRemote)
								worldIn.spawnEntity(bender);

						}
					}
				}
			}
			return false;
		}
	}
}
