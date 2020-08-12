package com.crowsofwar.avatar.client.model_loaders.wmb;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.crowsofwar.avatar.client.ResourceManager;
import com.crowsofwar.avatar.client.Vao;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Matrix4f;

public class WmbModel {

	protected Map<String, Vao> nameToVao = new HashMap<>();
	
	public void render(){
		for(Vao i : nameToVao.values()){
			GL20.glUseProgram(ResourceManager.test_shader);
			FloatBuffer buf = GLAllocation.createDirectFloatBuffer(16);
			
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
			Matrix4f mvMatrix = new Matrix4f();
			mvMatrix.load(buf);
			buf.rewind();
			
			GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, buf);
			Matrix4f pMatrix = new Matrix4f();
			pMatrix.load(buf);
			buf.rewind();
			
			Matrix4f.mul(mvMatrix, pMatrix, mvMatrix).store(buf);
			
			GL20.glUniformMatrix4(GL20.glGetUniformLocation(ResourceManager.test_shader, "modelViewProjectionMatrix"), false, buf);
			
			i.draw();
			//GL11.glCallList(i);
			//GL30.glBindVertexArray(i);
			//GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
		}
		//GL30.glBindVertexArray(0);
	}
}
