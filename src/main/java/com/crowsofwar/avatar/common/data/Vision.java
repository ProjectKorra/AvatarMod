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
	PURIFY_WEAK("shaders/post/purify_weak.json"),
	/**
	 * Purify vision for Level III
	 */
	PURIFY_MEDIUM("shaders/post/purify_medium.json"),
	/**
	 * Purify vision for Level IV
	 */
	PURIFY_POWERFUL("shaders/post/purify_powerful.json"),
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

	RESTORE("shaders/post/restore.json");

	private final ResourceLocation shaderLocation;

	private Vision(String shaderLocation) {
		this.shaderLocation = new ResourceLocation("avatarmod", shaderLocation);
	}

	public ResourceLocation getShaderLocation() {
		return shaderLocation;
	}

}
