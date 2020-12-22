package com.crowsofwar.avatar.client.model_loaders.obj;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public class ObjModel {

	protected List<Pair<String, Integer>> nameToCallList = new ArrayList<>();

	public void renderAll(){
		nameToCallList.forEach(list -> GL11.glCallList(list.getRight()));
	}
}
