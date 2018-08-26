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
import com.crowsofwar.avatar.common.entity.mob.EntityAirbender;
import com.crowsofwar.avatar.common.entity.mob.EntityFirebender;
import com.crowsofwar.avatar.common.entity.mob.EntityHumanBender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class HumanBenderSpawner {

/*	@SubscribeEvent
	public static void modifyVillageSpawner(InitMapGenEvent e) {

		if (e.getType() == EventType.VILLAGE) {
			// TODO See if this messes up superflat world options
			e.setNewGen(new MapGenVillageWithHumanbenders());


		}

	}**/

	@SubscribeEvent
	public static void modifyVillagerSpawns(LivingSpawnEvent event) {
		Entity e = event.getEntity();
		World world = e.getEntityWorld();
		if (event.getEntity() == e && e instanceof EntityVillager) {

			AxisAlignedBB box = new AxisAlignedBB(e.posX + 200,
					e.posY + 200, e.posZ + 200,
					e.posX - 200, e.posY - 200,
					e.posZ - 200);
			List<Entity> nearbyBenders = world.getEntitiesWithinAABB(EntityHumanBender.class, box);
			List<Entity> nearbyVillagers = world.getEntitiesWithinAABB(EntityVillager.class, box);
			int villagerSize = nearbyVillagers.size();
			int size = nearbyBenders.size();
			Random rand = new Random();
			boolean bender = rand.nextBoolean();
			//Will be changed when more benders are added
			if (size < MOBS_CONFIG.maxNumberOfBenders && villagerSize >= 5) {
				EntityHumanBender b = bender ? new EntityAirbender(world) : new EntityFirebender(world);
				b.copyLocationAndAnglesFrom(e);
				world.spawnEntity(b);
			}
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
					assert villager != null;
					return new ChunkPos(villager.getPosition()).equals(chunkCoord);
				});


				// To attempt to have all humanbenders be same type, check if
				// there are nearby humanbenders
				// If there are just copy their type
				AxisAlignedBB aabb = new AxisAlignedBB(villagers.get(0).posX + 100, villagers.get(0).posY + 100, villagers.get(0).posZ + 100,
						villagers.get(0).posX - 100, villagers.get(0).posY - 100, villagers.get(0).posZ - 100);
				List<EntityHumanBender> nearbyBenders = worldIn.getEntitiesWithinAABB(EntityHumanBender.class,
						aabb);
				Village village = worldIn.getVillageCollection()
						.getNearestVillage(chunkCoord.getBlock(0, 0, 0), 200);

				if (village != null) {
					EntityHumanBender airbender = new EntityAirbender(worldIn);
					airbender.setPosition(village.getCenter().getX(), village.getCenter().getY(), village.getCenter().getZ());
				}


				for (Entity e : villagers) {
					int i = rand.nextInt(3) + 1;
					if (i == 3) {
						EntityHumanBender b = new EntityAirbender(worldIn);
						b.setPosition(villagers.get(0).posX, villagers.get(0).posY, villagers.get(0).posZ);
					}
				}


				double chance = 100;
				Random rand = new Random();
				if (!villagers.isEmpty()/* && rand.nextDouble() * 100 < chance**/) {


					boolean firebender;

					if (nearbyBenders.isEmpty()) {
						firebender = new Random().nextBoolean();
					} else {
						firebender = nearbyBenders.get(0) instanceof EntityFirebender;
					}

					for (Entity e : villagers) {
						int i = rand.nextInt(3) + 1;
						if (i == 3) {
							EntityHumanBender bender = firebender ? new EntityFirebender(worldIn)
									: new EntityAirbender(worldIn);
							bender.copyLocationAndAnglesFrom(e);
							worldIn.spawnEntity(bender);

						}
					}
				}
			}

			return false;
		}

	}

}
