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

package com.crowsofwar.avatar.client.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class AvatarParticleAir extends AvatarParticle {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/particles/cloud.png");

	private static final ParticleFrame[] FRAMES = new ParticleFrame[8];

	static {
		for (int i = 0; i < FRAMES.length; i++) {
			FRAMES[i] = new ParticleFrame(TEXTURE, 256, (i % 4) * 64, i / 64, 64, 64);
		}
	}

	/**
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param velX
	 * @param velY
	 * @param velZ
	 */
	public AvatarParticleAir(int particleID, World world, double x, double y, double z, double velX,
							 double velY, double velZ, int... parameters) {
		super(world, x, y, z, velX, velY, velZ);

		particleScale = 4f;
		particleMaxAge *= 2;

		motionX = velX;
		motionY = velY;
		motionZ = velZ;

	}

	@Override
	protected ParticleFrame[] getTextureFrames() {
		return FRAMES;
	}

}
