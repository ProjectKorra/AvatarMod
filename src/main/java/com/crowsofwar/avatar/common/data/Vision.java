package com.crowsofwar.avatar.common.data;

import net.minecraft.util.ResourceLocation;

/**
 * Represents a type of "vision", or shader, to be applied to a player. Vision is applied by
 * using a shader, like the shaders from Super Secret Settings in 1.7, or when you spectate a mob.
 */
public enum Vision {

	/**
	 * Purify vision for level I-II
	 */
	IMMOLATE_WEAK("shaders/post/immolate_weak.json"),
	/**
	 * Purify vision for Level III
	 */
	IMMOLATE_MEDIUM("shaders/post/immolate_medium.json"),
	/**
	 * Purify vision for Level IV
	 */
	IMMOLATE_POWERFUL("shaders/post/immolate_powerful.json"),
	/**
	 * Slipstream vision for level I-II
	 */
	SLIPSTREAM_WEAK("shaders/post/slipstream_weak.json"),
	/**
	 * Slipstream vision for level III
	 */
	SLIPSTREAM_MEDIUM("shaders/post/slipstream_medium.json"),
	/**
	 * Slipstream vision for level IV
	 */
	SLIPSTREAM_POWERFUL("shaders/post/slipstream_powerful.json"),
	/**
	 * Restore vision for level I-II
	 */
	RESTORE_WEAK("shaders/post/restore_weak.json"),
	/**
	 * Restore vision for level III
	 */
	RESTORE_MEDIUM("shaders/post/restore_medium.json"),
	/**
	 * Restore vision for level IV
	 */
	RESTORE_POWERFUL("shaders/post/restore_powerful.json"),
	/**
	 * Cleanse vision for level I-II
	 */
	CLEANSE_WEAK("shaders/post/cleanse_weak.json"),
	/**
	 * Cleanse vision for level III
	 */
	CLEANSE_MEDIUM("shaders/post/cleanse_medium.json"),
	/**
	 * Cleanse vision for level IV
	 */
	CLEANSE_POWERFUL("shaders/post/cleanse_powerful.json");

	private final ResourceLocation shaderLocation;

	Vision(String shaderLocation) {
		this.shaderLocation = new ResourceLocation("avatarmod", shaderLocation);
	}

	public ResourceLocation getShaderLocation() {
		return shaderLocation;
	}

}
