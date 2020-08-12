package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.client.model_loaders.wmb.WmbLoader;
import com.crowsofwar.avatar.client.model_loaders.wmb.WmbModel;
import com.crowsofwar.avatar.client.shader.ShaderManager;

import net.minecraft.util.ResourceLocation;

public class ResourceManager {

	public static WmbModel test = WmbLoader.load(new ResourceLocation(AvatarInfo.MOD_ID, "models/et0110.wmb"));
	
	public static int test_shader = ShaderManager.loadShader(new ResourceLocation(AvatarInfo.MOD_ID, "shaders/glsl/base_vao"));
	
	public static void load(){
		
	}
}
