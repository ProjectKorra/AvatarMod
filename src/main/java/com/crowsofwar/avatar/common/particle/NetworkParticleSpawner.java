package com.crowsofwar.avatar.common.particle;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.network.packets.PacketCParticles;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

/**
 * A particle spawner which operates on the server thread. It sends packets to clients about
 * particles.
 * <p>
 * Avoid using spawnOneParticle as velocity might be unpredicted.
 * 
 * @author CrowsOfWar
 */
public class NetworkParticleSpawner implements ParticleSpawner {
	
	@Override
	public void spawnOneParticle(World world, AvatarParticleType particle, double x, double y, double z,
			double velocityX, double velocityY, double velocityZ, int... parameters) {
		
		// Velocity -> max velocity... results in not expected velocity client side.
		spawnParticles(world, particle, 1, 1, x, y, z, velocityX, velocityY, velocityZ, parameters);
		
	}
	
	@Override
	public void spawnParticles(World world, AvatarParticleType particle, int minimum, int maximum, double x,
			double y, double z, double maxVelocityX, double maxVelocityY, double maxVelocityZ,
			int... parameters) {
		
		TargetPoint point = new TargetPoint(world.provider.getDimension(), x, y, z, 64);
		
		AvatarMod.network.sendToAllAround(new PacketCParticles(particle.vanilla(), minimum, maximum, x, y, z,
				maxVelocityX / 20, maxVelocityY / 20, maxVelocityZ / 20), point);
		
	}
	
}
