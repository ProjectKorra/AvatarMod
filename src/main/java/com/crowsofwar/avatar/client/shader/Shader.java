package com.crowsofwar.avatar.client.shader;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL20;

public class Shader {

	private int shader;
	private List<Uniform> uniforms = new ArrayList<>(2);
	
	public Shader(int shader) {
		this.shader = shader;
	}
	
	public Shader withUniforms(Uniform... uniforms){
		for(Uniform u : uniforms){
			this.uniforms.add(u);
		}
		return this;
	}
	
	public void use(){
		GL20.glUseProgram(shader);
		for(Uniform u : uniforms){
			u.apply(shader);
		}
	}
	
	public static interface Uniform {
		public void apply(int shader);
	}
}
