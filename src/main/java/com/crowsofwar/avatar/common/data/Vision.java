package com.crowsofwar.avatar.common.data;

import net.minecraft.util.ResourceLocation;

/**
 * Represents a type of "vision", or shader, to be applied to a player. Vision is applied by
 * using a shader, like the shaders from Super Secret Settings in 1.7, or when you spectate a mob.
 */
public enum Vision {

	PURIFY("shaders/post/creeper.json");

	private final ResourceLocation shaderLocation;

	private Vision(String shaderLocation) {
		this.shaderLocation = new ResourceLocation("avatarmod", shaderLocation);
	}

	public ResourceLocation getShaderLocation() {
		return shaderLocation;
	}

}
