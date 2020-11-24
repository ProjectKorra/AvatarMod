package com.crowsofwar.avatar.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Vao {

	private int vao;
	private int drawMode;
	private int vertexCount;
	private int shaderProgram;
	
	public Vao(int vao, int drawMode, int vertexCount, int shaderProgram) {
		this.vao = vao;
		this.drawMode = drawMode;
		this.vertexCount = vertexCount;
		this.shaderProgram = shaderProgram;
	}
	
	public void draw(){
		//A value of 0 indicates that the user wants to apply the shader themselves
		if(shaderProgram != 0)
			GL20.glUseProgram(shaderProgram);
		GL30.glBindVertexArray(vao);
		GL11.glDrawElements(drawMode, vertexCount, GL11.GL_UNSIGNED_INT, 0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
	
	public void setProgram(int prog){
		this.shaderProgram = prog;
	}
}
