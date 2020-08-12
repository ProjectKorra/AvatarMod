package com.crowsofwar.avatar.client.model_loaders.wmb;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.crowsofwar.avatar.client.Vao;
import com.crowsofwar.avatar.client.model_loaders.wmb.WmbDataStructures.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;

public class WmbLoader {

	public static WmbModel load(ResourceLocation file) {
		try {
			InputStream s = Minecraft.getMinecraft().getResourceManager().getResource(file).getInputStream();
			ByteBuffer buf = ByteBuffer.wrap(IOUtils.toByteArray(s));
			buf.order(ByteOrder.LITTLE_ENDIAN);
			return parse(buf);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static WmbModel parse(ByteBuffer buf) throws Exception {
		WmbModel finalModel = new WmbModel();
		WmbHeader header = new WmbHeader(buf);

		buf.position(header.vertexHeaderIndex);
		VertexHeader vertHeader = new VertexHeader(buf);

		VertexData[] vertices = new VertexData[vertHeader.vertCount];
		for(int i = 0; i < vertHeader.vertCount; i++) {
			buf.position(vertHeader.vertArrayIndex + vertHeader.vertStride * i);
			vertices[i] = new VertexData(buf);
		}

		BoneWeight[] boneWeights = new BoneWeight[vertHeader.vertCount];
		buf.position(vertHeader.boneWeightArrayIndex);
		for(int i = 0; i < vertHeader.vertCount; i++) {
			boneWeights[i] = new BoneWeight(buf, vertHeader.boneWeightStride);
		}

		BoneMap[] boneMaps = new BoneMap[header.boneMapCount];
		buf.position(header.boneMapIndex);
		for(int i = 0; i < header.boneMapCount; i++) {
			boneMaps[i] = new BoneMap(buf);
		}

		Bone[] bones = new Bone[header.boneCount];
		buf.position(header.boneArrayIndex);
		for(int i = 0; i < header.boneCount; i++) {
			bones[i] = new Bone(buf);
		}

		Mesh[] meshes = new Mesh[header.meshCount];
		buf.position(header.meshArrayIndex);
		for(int i = 0; i < header.meshCount; i++) {
			meshes[i] = new Mesh(buf);
		}

		buf.position(header.meshMetaHeaderIndex);
		MeshMetadataHeader meshMetaHeader = new MeshMetadataHeader(buf);

		MeshMetadata[] meshMetas = new MeshMetadata[meshMetaHeader.meshMetaCount];
		buf.position(meshMetaHeader.meshMetaIndex);
		for(int i = 0; i < meshMetaHeader.meshMetaCount; i++) {
			meshMetas[i] = new MeshMetadata(buf);
		}

		NameHeader[] nameHeaders = new NameHeader[header.meshNameHeaderCount];
		buf.position(header.meshNameHeaderArrayIndex);
		for(int i = 0; i < header.meshNameHeaderCount; i++) {
			nameHeaders[i] = new NameHeader(buf);
		}

		// Vertex buffer [Vertex Buffer Object]
		int vbo = GL15.glGenBuffers();
		// pos tex normal bone indices bone weights
		int bytesPerVertex = 4 * 3 + 4 * 2 + 4 * 3 + 4 + 4;
		ByteBuffer data = GLAllocation.createDirectByteBuffer(vertHeader.vertCount * bytesPerVertex);
		for(int j = 0; j < vertHeader.vertCount; j++) {
			data.putFloat(vertices[j].x);
			data.putFloat(vertices[j].y);
			data.putFloat(vertices[j].z);
			data.putFloat(vertices[j].u);
			data.putFloat(vertices[j].v);
			data.putFloat(0);
			data.putFloat(1);
			data.putFloat(0);
			data.put(boneWeights[j].boneIndex0);
			data.put(boneWeights[j].boneIndex1);
			data.put(boneWeights[j].boneIndex2);
			data.put(boneWeights[j].boneIndex3);
			data.put(boneWeights[j].boneWeight0);
			data.put(boneWeights[j].boneWeight1);
			data.put(boneWeights[j].boneWeight2);
			data.put(boneWeights[j].boneWeight3);
		}
		data.rewind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);

		for(int i = 0; i < meshes.length; i++) {
			Mesh mesh = meshes[i];
			for(int j = 0; j < meshMetas.length; j++) {
				if(meshMetas[j].meshIndex == i) {
					mesh.meta = meshMetas[j];
					break;
				}
			}
			if(mesh.meta == null)
				continue;

			byte[] chars = new byte[16];
			buf.position(nameHeaders[mesh.meta.meshNameIndex].nameIndex);
			buf.get(chars);
			mesh.name = new String(chars, "UTF-8").trim();

			// Indices buffer [Element Buffer Object]
			int ebo = GL15.glGenBuffers();
			// Vertex Array Object
			int vao = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vao);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);

			// Positions, tex coords, normals
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, bytesPerVertex, 0);
			GL20.glEnableVertexAttribArray(0);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, bytesPerVertex, 12);
			GL20.glEnableVertexAttribArray(1);
			GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, bytesPerVertex, 20);
			GL20.glEnableVertexAttribArray(2);
			// Bone data (indices, weights)
			GL20.glVertexAttribPointer(3, 4, GL11.GL_UNSIGNED_BYTE, false, bytesPerVertex, 32);
			GL20.glEnableVertexAttribArray(3);
			GL20.glVertexAttribPointer(4, 4, GL11.GL_UNSIGNED_BYTE, true, bytesPerVertex, 36);
			GL20.glEnableVertexAttribArray(4);

			IntBuffer indices = GLAllocation.createDirectIntBuffer(mesh.faceCount);
			for(int j = 0; j < mesh.faceCount; j++) {
				int vertIndex = mesh.vertexStart + buf.getShort(vertHeader.faceArrayIndex + mesh.faceStart + j * 2);
				indices.put(vertIndex);
			}
			indices.rewind();
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

			GL30.glBindVertexArray(0);

			finalModel.nameToVao.put(mesh.name, new Vao(vao, GL11.GL_TRIANGLES, vertHeader.faceCount, 0));

			/*
			 * int list = GL11.glGenLists(1); GL11.glNewList(list,
			 * GL11.GL_COMPILE);
			 * Tessellator.getInstance().getBuffer().begin(GL11.GL_TRIANGLES,
			 * DefaultVertexFormats.POSITION_TEX); for(int j = 0; j <
			 * mesh.faceCount; j ++){ int vertIndex = mesh.vertexStart +
			 * buf.getShort(vertHeader.faceArrayIndex + mesh.faceStart + j*2);
			 * VertexData vertex = vertices[vertIndex];
			 * Tessellator.getInstance().getBuffer().pos(vertex.x, vertex.y,
			 * vertex.z).tex(vertex.u, vertex.v).endVertex(); }
			 * Tessellator.getInstance().draw(); GL11.glEndList();
			 * finalModel.nameToVao.put(mesh.name, list);
			 */
		}

		System.out.println(boneMaps.length + " " + bones.length + " " + bones[0].posAbsoluteX);
		System.out.println(meshes[0].name);
		return finalModel;
	}
}
