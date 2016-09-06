package com.crowsofwar.avatar.common;

import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.EnumHelper;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarParticles {
	
	private static EnumParticleTypes particleFlames;
	
	public static void register() {
		EnumHelper.addEnum(EnumParticleTypes.class, "AVATAR_FLAMES",
				new Class<?>[] { String.class, int.class, boolean.class }, "avatarflame", nextParticleId(),
				true);
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
	
}
