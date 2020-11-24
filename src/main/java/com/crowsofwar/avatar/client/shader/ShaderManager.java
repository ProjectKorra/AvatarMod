package com.crowsofwar.avatar.client.shader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.client.ResourceManager;
import com.crowsofwar.avatar.client.shader.Shader.Uniform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.util.ResourceLocation;

public class ShaderManager {

	public static final FloatBuffer AUX_GL_BUFFER = GLAllocation.createDirectFloatBuffer(16);
	
	public static final Uniform MODELVIEW_PROJECTION_MATRIX = shader -> {
		//No idea if all these rewind calls are necessary. I'll have to check that later.
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, AUX_GL_BUFFER);
		AUX_GL_BUFFER.rewind();
		Matrix4f mvMatrix = new Matrix4f();
		mvMatrix.load(AUX_GL_BUFFER);
		AUX_GL_BUFFER.rewind();
		
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, AUX_GL_BUFFER);
		AUX_GL_BUFFER.rewind();
		Matrix4f pMatrix = new Matrix4f();
		pMatrix.load(AUX_GL_BUFFER);
		AUX_GL_BUFFER.rewind();
		
		Matrix4f.mul(pMatrix, mvMatrix, mvMatrix).store(AUX_GL_BUFFER);
		AUX_GL_BUFFER.rewind();
		
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(shader, "modelViewProjectionMatrix"), false, AUX_GL_BUFFER);
	};
	
	//TODO Make this return a shader object instead of the id
	public static int loadShader(ResourceLocation file) {
		int vertexShader = 0;
		int fragmentShader = 0;
		try {
			int program = GL20.glCreateProgram();
			
			vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
			GL20.glShaderSource(vertexShader, readFileToBuf(new ResourceLocation(file.getNamespace(), file.getPath() + ".vert")));
			GL20.glCompileShader(vertexShader);
			if(GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
				AvatarLog.error(GL20.glGetShaderInfoLog(vertexShader, GL20.GL_INFO_LOG_LENGTH));
				throw new RuntimeException("Error creating vertex shader: " + file);
			}
			
			fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
			GL20.glShaderSource(fragmentShader, readFileToBuf(new ResourceLocation(file.getNamespace(), file.getPath() + ".frag")));
			GL20.glCompileShader(fragmentShader);
			if(GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
				AvatarLog.error(GL20.glGetShaderInfoLog(fragmentShader, GL20.GL_INFO_LOG_LENGTH));
				throw new RuntimeException("Error creating fragment shader: " + file);
			}
			
			GL20.glAttachShader(program, vertexShader);
			GL20.glAttachShader(program, fragmentShader);
			GL20.glLinkProgram(program);
			if(GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
				AvatarLog.error(GL20.glGetProgramInfoLog(program, GL20.GL_INFO_LOG_LENGTH));
				throw new RuntimeException("Error creating fragment shader: " + file);
			}
			
			GL20.glDeleteShader(vertexShader);
			GL20.glDeleteShader(fragmentShader);
			
			return program;
		} catch(Exception x) {
			GL20.glDeleteShader(vertexShader);
			GL20.glDeleteShader(fragmentShader);
			x.printStackTrace();
		}
		return 0;
	}

	private static ByteBuffer readFileToBuf(ResourceLocation file) throws IOException {
		InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(file).getInputStream();
		byte[] bytes = IOUtils.toByteArray(in);
		IOUtils.closeQuietly(in);
		ByteBuffer buf = BufferUtils.createByteBuffer(bytes.length);
		buf.put(bytes);
		buf.rewind();
		return buf;
	}
}
