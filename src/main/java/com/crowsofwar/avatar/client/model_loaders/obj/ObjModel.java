package com.crowsofwar.avatar.client.model_loaders.obj;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

public class ObjModel {

	protected Map<String, Integer> nameToCallList = new HashMap<>();
	
	public void renderAll(){
		nameToCallList.values().forEach(list -> GL11.glCallList(list));
	}
	
}
