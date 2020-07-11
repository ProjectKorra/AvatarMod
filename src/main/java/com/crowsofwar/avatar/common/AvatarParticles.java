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

package com.crowsofwar.avatar.common;

import com.google.common.collect.Maps;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.EnumHelper;

import java.util.HashMap;
import java.util.Map;

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;

/**
 * Manages registration of custom particles
 *
 * @author CrowsOfWar
 */
public class AvatarParticles {

	private static EnumParticleTypes particleFlames, particleAir, particleRestore, particleElectricity, particleBigFlames, particleFire;
	private static Map<Integer, EnumParticleTypes> lookup;
	private static Map<String, EnumParticleTypes> name;

	public static void register() {
		lookup = new HashMap<>();
		name = new HashMap<>();
		particleFlames = addParticle("flames");
		particleFire = addParticle("fire");
		particleBigFlames = addParticle("big_flame");
		particleAir = addParticle("air");
		particleRestore = addParticle("restore");
		particleElectricity = addParticle("electricity");

	}

	private static EnumParticleTypes addParticle(String particleName) {

		EnumParticleTypes particle = EnumHelper.addEnum(EnumParticleTypes.class,
				"AVATAR_" + particleName.toUpperCase(),
				new Class<?>[]{String.class, int.class, boolean.class},
				"avatar" + particleName.substring(0, 1).toUpperCase()
						+ particleName.substring(1).toLowerCase(),
				nextParticleId(), true);

		lookup.put(particle.getParticleID(), particle);
		name.put(particle.getParticleName(), particle);
		return particle;

	}

	private static int nextParticleId() {
		EnumParticleTypes[] allParticles = EnumParticleTypes.values();
		int maxId = -1;
		for (EnumParticleTypes particle : allParticles) {
			if (particle.getParticleID() > maxId) {
				maxId = particle.getParticleID();
			}
		}
		return maxId + 1;
	}

	public static EnumParticleTypes getParticleFromName(String particleName) {
		return name.get(particleName);
	}

	public static EnumParticleTypes getParticleFlames() {
		return CLIENT_CONFIG.useCustomParticles ? particleFlames : EnumParticleTypes.FLAME;
	}

	public static EnumParticleTypes getParticleFire() {
		return CLIENT_CONFIG.useCustomParticles ? particleFire : EnumParticleTypes.FLAME;
	}

	public static EnumParticleTypes getParticleBigFlame() {
		return CLIENT_CONFIG.useCustomParticles ? particleBigFlames : EnumParticleTypes.FLAME;
	}

	public static EnumParticleTypes getParticleAir() {
		return CLIENT_CONFIG.useCustomParticles ? particleAir : EnumParticleTypes.CLOUD;
	}

	public static EnumParticleTypes getParticleRestore() {
		return CLIENT_CONFIG.useCustomParticles ? particleRestore : EnumParticleTypes.VILLAGER_HAPPY;
	}

	public static EnumParticleTypes getParticleElectricity() {
		return CLIENT_CONFIG.useCustomParticles ? particleElectricity : EnumParticleTypes.ENCHANTMENT_TABLE;
	}

	/**
	 * Looks up that particle. Returns null if none found.
	 */
	public static EnumParticleTypes lookup(int id) {
		return lookup.get(id);
	}

}
