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
package com.crowsofwar.avatar.entity.ai;

import com.crowsofwar.avatar.entity.data.AnimalCondition;
import com.crowsofwar.avatar.entity.data.BisonSpawnData;
import com.crowsofwar.avatar.entity.mob.EntitySkyBison;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static com.crowsofwar.avatar.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityAiBisonBreeding extends EntityAIBase {

	private final EntitySkyBison bison;

	public EntityAiBisonBreeding(EntitySkyBison bison) {
		this.bison = bison;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		AnimalCondition cond = bison.getCondition();
		return cond.isReadyToBreed() && getNearbyBison() > 0;
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public boolean shouldContinueExecuting() {

		if (!shouldExecute()) {
			return false;
		}

		double range = 100;

		Vector pos = Vector.getEntityPos(bison);
		Vector min = pos.minus(range / 2, range / 2, range / 2);
		Vector max = pos.plus(range / 2, range / 2, range / 2);

		AxisAlignedBB aabb = new AxisAlignedBB(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());

		List<EntitySkyBison> mates = bison.world.getEntitiesWithinAABB(EntitySkyBison.class, aabb,
				b -> b != bison && b.getCondition().isReadyToBreed());

		if (!mates.isEmpty()) {
			EntitySkyBison mate = mates.get(0);
			if (getNearbyBison() < 15) {
				bison.getMoveHelper().setMoveTo(mate.posX, mate.posY, mate.posZ, 1);
				// 7 obtained through real-world testing
				if (bison.getDistanceSq(mate) <= 7) {

					spawnBaby(mate);

					bison.getCondition().setBreedTimer(generateBreedTimer());
					mate.getCondition().setBreedTimer(generateBreedTimer());

					return true;

				}
			}
		}

		return true;

	}

	private void spawnBaby(EntitySkyBison mate) {

		World world = bison.world;
		AnimalCondition cond = bison.getCondition();
		EntitySkyBison child = new EntitySkyBison(world);

		if (child != null) {

			// Spawn the baby
			child.getCondition().setAge(0);
			child.setLocationAndAngles(bison.posX, bison.posY, bison.posZ, 0, 0);
			child.onInitialSpawn(world.getDifficultyForLocation(bison.getPosition()),
					new BisonSpawnData(true));
			world.spawnEntity(child);

			// Spawn heart particles
			Random random = bison.getRNG();
			for (int i = 0; i < 7; ++i) {

				double mx = random.nextGaussian() * 0.02D;
				double my = random.nextGaussian() * 0.02D;
				double mz = random.nextGaussian() * 0.02D;

				double dx = random.nextDouble() * bison.width * 2 - bison.width;
				double dy = 0.5 + random.nextDouble() * bison.height;
				double dz = random.nextDouble() * bison.width * 2 - bison.width;

				world.spawnParticle(EnumParticleTypes.HEART, bison.posX + dx, bison.posY + dy,
						bison.posZ + dz, mx, my, mz);

			}

			// Spawn XP orbs
			if (world.getGameRules().getBoolean("doMobLoot")) {
				world.spawnEntity(
						new EntityXPOrb(world, bison.posX, bison.posY, bison.posZ, random.nextInt(7) + 1));
			}

		}
	}

	private int generateBreedTimer() {
		Random random = bison.getRNG();
		float min = MOBS_CONFIG.bisonSettings.bisonBreedMinMinutes;
		float max = MOBS_CONFIG.bisonSettings.bisonBreedMaxMinutes;
		float minutes = min + random.nextFloat() * (max - min);
		return (int) (minutes * 1200);
	}

	/**
	 * Get the number of nearby other bison, excluding this bison.
	 */
	private int getNearbyBison() {

		World world = bison.world;

		AxisAlignedBB aabb = new AxisAlignedBB(bison.posX - 32, 0, bison.posZ - 32, bison.posX + 32, 255,
				bison.posZ + 32);

		return world.getEntitiesWithinAABB(EntitySkyBison.class, aabb, b -> b != bison).size();

	}

}
