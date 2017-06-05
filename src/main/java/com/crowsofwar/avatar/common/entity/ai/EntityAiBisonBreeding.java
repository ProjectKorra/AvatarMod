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
package com.crowsofwar.avatar.common.entity.ai;

import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

import java.util.Random;

import com.crowsofwar.avatar.common.entity.data.AnimalCondition;
import com.crowsofwar.avatar.common.entity.data.BisonSpawnData;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * 
 * 
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
		return !cond.isSterile() && cond.getBreedTimer() == 0 && cond.isAdult();
	}
	
	@Override
	public void startExecuting() {
		bison.setLoveParticles(true);
	}
	
	@Override
	public boolean continueExecuting() {
		
		if (!shouldExecute()) {
			bison.setLoveParticles(false);
			return false;
		}
		
		double range = 100;
		
		Vector pos = Vector.getEntityPos(bison);
		Vector min = pos.minus(range / 2, range / 2, range / 2);
		Vector max = pos.plus(range / 2, range / 2, range / 2);
		
		AxisAlignedBB aabb = new AxisAlignedBB(min.toMinecraft(), max.toMinecraft());
		
		EntitySkyBison nearest = bison.worldObj.findNearestEntityWithinAABB(EntitySkyBison.class, aabb,
				bison);
		if (nearest != null) {
			bison.getMoveHelper().setMoveTo(nearest.posX, nearest.posY, nearest.posZ, 1);
			// 7 obtained through real-world testing
			if (bison.getDistanceSqToEntity(nearest) <= 7) {
				
				spawnBaby(nearest);
				
				bison.getCondition().setBreedTimer(generateBreedTimer());
				nearest.getCondition().setBreedTimer(generateBreedTimer());
				bison.setLoveParticles(false);
				nearest.setLoveParticles(false);
				
				return true;
				
			}
		}
		
		return true;
		
	}
	
	private void spawnBaby(EntitySkyBison mate) {
		
		World world = bison.worldObj;
		AnimalCondition cond = bison.getCondition();
		EntitySkyBison child = new EntitySkyBison(world);
		
		if (child != null) {
			
			// Spawn the baby
			child.getCondition().setAge(0);
			child.setLocationAndAngles(bison.posX, bison.posY, bison.posZ, 0, 0);
			child.onInitialSpawn(world.getDifficultyForLocation(bison.getPosition()),
					new BisonSpawnData(true));
			world.spawnEntityInWorld(child);
			
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
				world.spawnEntityInWorld(
						new EntityXPOrb(world, bison.posX, bison.posY, bison.posZ, random.nextInt(7) + 1));
			}
			
		}
	}
	
	private int generateBreedTimer() {
		Random random = bison.getRNG();
		float min = MOBS_CONFIG.bisonBreedMinMinutes;
		float max = MOBS_CONFIG.bisonBreedMaxMinutes;
		float minutes = min + random.nextFloat() * (max - min);
		return (int) (minutes * 1200);
	}
	
}
