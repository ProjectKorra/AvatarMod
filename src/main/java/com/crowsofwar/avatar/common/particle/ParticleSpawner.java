package com.crowsofwar.avatar.common.particle;

import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.world.World;

/**
 * Handles particle spawning.
 * 
 * @author CrowsOfWar
 */
public interface ParticleSpawner {
	
	/**
	 * Spawn a particle in the world manually. It's generally recommended to
	 * {@link #spawnParticles(World, AvatarParticleType, int, double, double, double, double, double, double, int...)
	 * spawn multiple particles}.
	 * 
	 * @param world
	 *            The world
	 * @param particle
	 *            The type of particle
	 * @param x
	 *            X-position of particle
	 * @param y
	 *            Y-position of particle
	 * @param z
	 *            Z-position of particle
	 * @param velocityX
	 *            Velocity X of particle
	 * @param velocityY
	 *            Velocity Y of particle
	 * @param velocityZ
	 *            Velocity Z of particle
	 * @param parameters
	 *            Extra parameters for the particle effect
	 */
	void spawnOneParticle(World world, AvatarParticleType particle, double x, double y, double z,
			double velocityX, double velocityY, double velocityZ, int... parameters);
	
	/**
	 * Spawn a particle in the world manually. It's generally recommended to
	 * {@link #spawnParticles(World, AvatarParticleType, int, Vector, Vector, int...) spawn multiple
	 * particles}.
	 * 
	 * @param world
	 *            The world
	 * @param particle
	 *            The type of particle
	 * @param position
	 *            Position of particle
	 * @param velocity
	 *            Velocity of particle
	 * @param parameters
	 *            Extra parameters for the particle effect
	 */
	default void spawnOneParticle(World world, AvatarParticleType particle, Vector position, Vector velocity,
			int... parameters) {
		spawnOneParticle(world, particle, position.x(), position.y(), position.z(), velocity.x(),
				velocity.y(), velocity.z(), parameters);
	}
	
	/**
	 * Spawn multiple particles in the world. This is better than spawning particles manually since
	 * it can be optimized for different settings.
	 * 
	 * @param world
	 *            The world
	 * @param particle
	 *            Type of particle
	 * @param minimum
	 *            Minimum amount of particles to spawn
	 * @param maximum
	 *            Maximum amount of particles to spawn
	 * @param x
	 *            X-position to spawn at
	 * @param y
	 *            Y-position to spawn at
	 * @param z
	 *            Z-position to spawn at
	 * @param maxVelocityX
	 *            Maximum velocity X
	 * @param maxVelocityY
	 *            Maximum velocity Y
	 * @param maxVelocityZ
	 *            Maximum velocity Z
	 * @param parameters
	 *            Extra parameters for the particle effect
	 */
	void spawnParticles(World world, AvatarParticleType particle, int minimum, int maximum, double x,
			double y, double z, double maxVelocityX, double maxVelocityY, double maxVelocityZ,
			int... parameters);
	
	/**
	 * Spawn multiple particles in the world. This is better than spawning particles manually since
	 * it can be optimized for different settings.
	 * 
	 * @param world
	 *            The world
	 * @param particle
	 *            Type of particle
	 * @param minimum
	 *            Minimum amount of particles to spawn
	 * @param maximum
	 *            Maximum amount of particles to spawn
	 * @param position
	 *            Position to spawn at
	 * @param maxVelocity
	 *            Maximum velocity of particles
	 * @param parameters
	 *            Extra parameters for the particle effect
	 */
	default void spawnParticles(World world, AvatarParticleType particle, int minimum, int maximum,
			Vector position, Vector maxVelocity, int... parameters) {
		spawnParticles(world, particle, minimum, maximum, position.x(), position.y(), position.z(),
				maxVelocity.x(), maxVelocity.y(), maxVelocity.z(), parameters);
	}
	
}
