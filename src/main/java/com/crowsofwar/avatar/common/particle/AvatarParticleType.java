package com.crowsofwar.avatar.common.particle;

import com.crowsofwar.avatar.common.AvatarParticles;

import net.minecraft.util.EnumParticleTypes;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public enum AvatarParticleType {
	
	FLAMES(AvatarParticles.getParticleFlames()),
	AIR(AvatarParticles.getParticleAir());
	
	private final EnumParticleTypes vanillaType;
	
	private AvatarParticleType(EnumParticleTypes vanillaType) {
		this.vanillaType = vanillaType;
	}
	
	public EnumParticleTypes vanilla() {
		return vanillaType;
	}
	
}
