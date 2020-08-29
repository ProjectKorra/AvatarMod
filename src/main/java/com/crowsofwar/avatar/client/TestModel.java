package com.crowsofwar.avatar.client;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;

public class TestModel {
	
	public static TestModel test = new TestModel();

	int vao;

	public TestModel() {
		float vertices[] = { 
				0.5f, 0.5f, 0.0f, // top right
				0.5f, -0.5f, 0.0f, // bottom right
				-0.5f, -0.5f, 0.0f, // bottom left
				-0.5f, 0.5f, 0.0f // top left
		};
		FloatBuffer vert_buf = GLAllocation.createDirectFloatBuffer(12);
		vert_buf.put(vertices);
		vert_buf.rewind();
		
		int indices[] = { // note that we start from 0!
				0, 1, 3, // first Triangle
				1, 2, 3 // second Triangle
		};
		IntBuffer ind_buf = GLAllocation.createDirectIntBuffer(6);
		ind_buf.put(indices);
		ind_buf.rewind();
		
		int vao = GL30.glGenVertexArrays();
		int vbo = GL15.glGenBuffers();
		int ebo = GL15.glGenBuffers();
		GL30.glBindVertexArray(vao);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vert_buf, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ind_buf, GL15.GL_STATIC_DRAW);
		
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 12, 0);
		GL20.glEnableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL30.glBindVertexArray(0);
		this.vao = vao;
	}

	public void render() {
		GL20.glUseProgram(ResourceManager.test_shader);
		FloatBuffer buf = GLAllocation.createDirectFloatBuffer(16);
		
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
		buf.rewind();
		Matrix4f mvMatrix = new Matrix4f();
		mvMatrix.load(buf);
		buf.rewind();
		
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, buf);
		buf.rewind();
		Matrix4f pMatrix = new Matrix4f();
		pMatrix.load(buf);
		buf.rewind();
		
		Matrix4f.mul(pMatrix, mvMatrix, mvMatrix).store(buf);
		buf.rewind();
		
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(ResourceManager.test_shader, "modelViewProjectionMatrix"), false, buf);
		GL30.glBindVertexArray(vao);
		GlStateManager.disableCull();
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
}
