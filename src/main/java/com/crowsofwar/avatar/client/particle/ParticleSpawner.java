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

package com.crowsofwar.avatar.client.particle;

import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

/**
 * Handles particle spawning.
 *
 * @author CrowsOfWar
 */
public interface ParticleSpawner {

	/**
	 * Spawn a particle in the world manually. It's generally recommended to
	 * {@link #spawnParticles(World, ParticleType, int, double, double, double, double, double, double, int...)
	 * spawn multiple particles}.
	 *
	 * @param world      The world
	 * @param particle   The type of particle
	 * @param x          X-position of particle
	 * @param y          Y-position of particle
	 * @param z          Z-position of particle
	 * @param velocityX  Velocity X of particle in m/s
	 * @param velocityY  Velocity Y of particle in m/s
	 * @param velocityZ  Velocity Z of particle in m/s
	 * @param parameters Extra parameters for the particle effect
	 */
	void spawnOneParticle(World world, EnumParticleTypes particle, double x, double y, double z,
						  double velocityX, double velocityY, double velocityZ, int... parameters);

	/**
	 * Spawn a particle in the world manually. It's generally recommended to
	 * {@link #spawnParticles(World, ParticleType, int, Vector, Vector, int...)
	 * spawn multiple particles}.
	 *
	 * @param world      The world
	 * @param particle   The type of particle
	 * @param position   Position of particle
	 * @param velocity   Velocity of particle in m/s
	 * @param parameters Extra parameters for the particle effect
	 */
	default void spawnOneParticle(World world, EnumParticleTypes particle, Vector position, Vector velocity,
								  int... parameters) {
		spawnOneParticle(world, particle, position.x(), position.y(), position.z(), velocity.x(),
				velocity.y(), velocity.z(), parameters);
	}

	/**
	 * Spawn multiple particles in the world. This is better than spawning
	 * particles manually since it can be optimized for different settings.
	 *
	 * @param world        The world
	 * @param particle     Type of particle
	 * @param minimum      Minimum amount of particles to spawn
	 * @param maximum      Maximum amount of particles to spawn
	 * @param x            X-position to spawn at
	 * @param y            Y-position to spawn at
	 * @param z            Z-position to spawn at
	 * @param maxVelocityX Maximum velocity X in m/s
	 * @param maxVelocityY Maximum velocity Y in m/s
	 * @param maxVelocityZ Maximum velocity Z in m/s
	 * @param parameters   Extra parameters for the particle effect
	 */
	void spawnParticles(World world, EnumParticleTypes particle, int minimum, int maximum, double x, double y,
						double z, double maxVelocityX, double maxVelocityY, double maxVelocityZ, boolean velIsMagnitude, int... parameters);

	/**
	 * Spawn multiple particles in the world. This is better than spawning
	 * particles manually since it can be optimized for different settings.
	 *
	 * @param world       The world
	 * @param particle    Type of particle
	 * @param minimum     Minimum amount of particles to spawn
	 * @param maximum     Maximum amount of particles to spawn
	 * @param position    Position to spawn at
	 * @param maxVelocity Maximum velocity of particles in m/s
	 * @param parameters  Extra parameters for the particle effect
	 */
	default void spawnParticles(World world, EnumParticleTypes particle, int minimum, int maximum,
								Vector position, Vector maxVelocity, boolean velIsMagnitude, int... parameters) {
		spawnParticles(world, particle, minimum, maximum, position.x(), position.y(), position.z(),
				maxVelocity.x(), maxVelocity.y(), maxVelocity.z(), velIsMagnitude, parameters);
	}

}
