package com.crowsofwar.avatar.common;

import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.EnumHelper;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarParticles {
	
	private static EnumParticleTypes particleFlames, particleAir;
	
	public static void register() {
		particleFlames = addParticle("flames");
		particleAir = addParticle("air");
	}
	
	private static EnumParticleTypes addParticle(String particleName) {
		
		return EnumHelper.addEnum(EnumParticleTypes.class, "AVATAR_" + particleName.toUpperCase(),
				new Class<?>[] { String.class, int.class, boolean.class },
				"avatar" + particleName.substring(0, 1).toUpperCase()
						+ particleName.substring(1).toLowerCase(),
				nextParticleId(), true);
		
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
	
	public static EnumParticleTypes getParticleFlames() {
		return particleFlames;
	}
	
	public static EnumParticleTypes getParticleAir() {
		return particleAir;
	}
	
}
