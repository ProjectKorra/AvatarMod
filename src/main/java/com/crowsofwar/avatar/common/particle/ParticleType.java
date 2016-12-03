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

import com.crowsofwar.avatar.common.AvatarParticles;

import net.minecraft.util.EnumParticleTypes;

/**
 * Particles used by AvatarMod. They include custom particles and regular
 * particles.
 * 
 * @author CrowsOfWar
 */
public enum ParticleType {
	
	FLAMES(AvatarParticles.getParticleFlames()),
	AIR(AvatarParticles.getParticleAir()),
	CLOUD(EnumParticleTypes.CLOUD);
	
	private final EnumParticleTypes vanillaType;
	
	private ParticleType(EnumParticleTypes vanillaType) {
		this.vanillaType = vanillaType;
	}
	
	public EnumParticleTypes vanilla() {
		return vanillaType;
	}
	
	public static ParticleType lookup(int id) {
		if (id < 0 || id >= values().length)
			throw new IllegalArgumentException("Cannot lookup invalid particle with ID: " + id);
		return values()[id];
	}
	
}
