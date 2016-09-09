package com.crowsofwar.avatar.common.particle;

import java.util.Random;

import com.crowsofwar.avatar.AvatarMod;

import net.minecraft.world.World;

/**
 * An implementation of {@link ParticleSpawner} which spawns particles client-side.
 * <p>
 * This can be used on either side
 * 
 * @author CrowsOfWar
 */
public class ClientParticleSpawner implements ParticleSpawner {
	
	@Override
	public void spawnOneParticle(World world, AvatarParticleType particle, double x, double y, double z,
			double velocityX, double velocityY, double velocityZ, int... parameters) {
		
		if (world.isRemote) {
			
			world.spawnParticle(particle.vanilla(), x, y, z, velocityX, velocityY, velocityZ, parameters);
			
		}
		
	}
	
	@Override
	public void spawnParticles(World world, AvatarParticleType particle, int minimum, int maximum, double x,
			double y, double z, double maxVelocityX, double maxVelocityY, double maxVelocityZ,
			int... parameters) {
		
		if (world.isRemote) {
			
			Random random = new Random();
			
			int particlesToSpawn = (int) ((random.nextGaussian() * (maximum - minimum)) + minimum);
			int particleSetting = 1 + AvatarMod.proxy.getParticleAmount();// 1+0=1,1+1=2,1+2=3
			particlesToSpawn /= particleSetting;
			if (particlesToSpawn == 0 && random.nextInt(8) == 0) particlesToSpawn = 1;
			
			for (int i = 0; i < particlesToSpawn; i++) {
				
				world.spawnParticle(particle.vanilla(), x, y, z, random(random, -maxVelocityX, maxVelocityX),
						random(random, -maxVelocityY, maxVelocityY),
						random(random, -maxVelocityZ, maxVelocityZ), parameters);
				
			}
			
		}
		
	}
	
	private double random(Random rand, double min, double max) {
		return min + rand.nextGaussian() * (max - min);
	}
	
}
