package com.crowsofwar.avatar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class AvatarShaderUtils {


	private static EntityRenderer getEntityRenderer() {
		return Minecraft.getMinecraft().entityRenderer;
	}

	/**
	 * Gets the current shader in use as a string, for example minecraft:shaders/post/creeper.json
	 */
	@Nullable
	public static String getCurrentShader() {
		if (getEntityRenderer().getShaderGroup() == null) {
			return null;
		}
		return getEntityRenderer().getShaderGroup().getShaderGroupName();
	}

	/**
	 * Uses the given shader. Can be used many times in a row because it doesn't recompile shader
	 * if the shader has already been equipped.
	 */
	public static void useShader(ResourceLocation shader) {
		if (!getCurrentShader().equals(shader.toString())) {
			getEntityRenderer().loadShader(shader);
		}
	}

	public static void stopUsingShader() {
		getEntityRenderer().stopUseShader();
	}

}
