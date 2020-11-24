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

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.network.packets.PacketCParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

/**
 * A particle spawner which operates on the server thread. It sends packets to
 * clients about particles.
 * <p>
 * Avoid using spawnOneParticle as velocity might be unpredicted.
 *
 * @author CrowsOfWar
 */
public class NetworkParticleSpawner implements ParticleSpawner {

	@Override
	public void spawnOneParticle(World world, EnumParticleTypes particle, double x, double y, double z,
								 double velocityX, double velocityY, double velocityZ, int... parameters) {

		// Velocity -> max velocity... results in not expected velocity client
		// side.
		spawnParticles(world, particle, 1, 1, x, y, z, velocityX, velocityY, velocityZ, false, parameters);

	}



	@Override
	public void spawnParticles(World world, EnumParticleTypes particle, int minimum, int maximum, double x,
							   double y, double z, double maxVelocityX, double maxVelocityY,double maxVelocityZ,
							   boolean velIsMagnitude,  int... parameters) {
		if (!world.isRemote) {
			TargetPoint point = new TargetPoint(world.provider.getDimension(), x, y, z, 96);

			AvatarMod.network.sendToAllAround(new PacketCParticles(particle, minimum, maximum, x, y, z,
					maxVelocityX / 20, maxVelocityY / 20, maxVelocityZ / 20, velIsMagnitude), point);
		}

	}

}
