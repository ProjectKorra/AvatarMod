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

package com.crowsofwar.avatar.common.particle;

import java.util.Random;

import com.crowsofwar.avatar.AvatarMod;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

/**
 * An implementation of {@link ParticleSpawner} which spawns particles
 * client-side.
 * <p>
 * This can be used on either side
 * 
 * @author CrowsOfWar
 */
public class ClientParticleSpawner implements ParticleSpawner {
	
	@Override
	public void spawnOneParticle(World world, EnumParticleTypes particle, double x, double y, double z,
			double velocityX, double velocityY, double velocityZ, int... parameters) {
		
		if (world.isRemote) {
			
			world.spawnParticle(particle, x, y, z, velocityX / 20, velocityY / 20, velocityZ / 20,
					parameters);
			
		}
		
	}
	
	@Override
	public void spawnParticles(World world, EnumParticleTypes particle, int minimum, int maximum, double x,
			double y, double z, double maxVelocityX, double maxVelocityY, double maxVelocityZ,
			int... parameters) {
		
		if (world.isRemote) {
			
			Random random = new Random();
			
			int particlesToSpawn = (int) ((random.nextGaussian() * (maximum - minimum)) + minimum);
			int particleSetting = 1 + AvatarMod.proxy.getParticleAmount();// 1+0=1,1+1=2,1+2=3
			particlesToSpawn /= particleSetting;
			if (particlesToSpawn == 0 && random.nextInt(8) == 0) particlesToSpawn = 1;
			
			for (int i = 0; i < particlesToSpawn; i++) {
				
				world.spawnParticle(particle, x, y, z, random(random, -maxVelocityX, maxVelocityX) / 20,
						random(random, -maxVelocityY, maxVelocityY) / 20,
						random(random, -maxVelocityZ, maxVelocityZ) / 20, parameters);
				
			}
			
		}
		
	}
	
	private double random(Random rand, double min, double max) {
		return min + rand.nextGaussian() * (max - min);
	}
	
}
