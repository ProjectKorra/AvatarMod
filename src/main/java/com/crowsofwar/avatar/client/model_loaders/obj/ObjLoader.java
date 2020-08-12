package com.crowsofwar.avatar.client.model_loaders.obj;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.AvatarLog;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class ObjLoader {

	private static ArrayList<Vec3d> vertices = new ArrayList<>();
	private static ArrayList<Vec2f> texCoords = new ArrayList<>();
	private static ArrayList<Vec3d> normals = new ArrayList<>();
	private static String currentName = "";
	
	public static ObjModel load(ResourceLocation model){
		try {
			BufferedReader read = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(model).getInputStream()));
			currentName = model.toString();
			return parseObj(read);
		} catch(Exception e) {
			AvatarLog.error("Failed to load model: " + model, e);
		}
		return null;
	}
	
	private static ObjModel parseObj(BufferedReader read) throws Exception {
		vertices.clear();
		texCoords.clear();
		normals.clear();
		
		String currentLine = null;
		String name = "";
		int lineNumber = 0;
		int currentList = 0;
		boolean isDrawing = false;
		ObjModel model = new ObjModel();
		while((currentLine = read.readLine()) != null){
			lineNumber++;
			currentLine = currentLine.trim();
			if(currentLine.startsWith("#") || currentLine.isEmpty()){
				continue;
			} else if(currentLine.startsWith("o") || currentLine.startsWith("g")){
				name = currentLine.split(" ")[1].trim();
				if(model.nameToCallList.containsKey(name)){
					GL11.glDeleteLists(model.nameToCallList.get(name), 1);
				}
				if(isDrawing){
					Tessellator.getInstance().draw();
					GL11.glEndList();
				}
				
				isDrawing = true;
				Tessellator.getInstance().getBuffer().begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
				currentList = GL11.glGenLists(1);
				GL11.glNewList(currentList, GL11.GL_COMPILE);
				model.nameToCallList.put(name, currentList);
			} else if(currentLine.startsWith("v ")){
				String[] values = currentLine.split(" ");
				if(values.length == 4){
					try {
						vertices.add(new Vec3d(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
					} catch(Exception e){
						throw new Exception("Model " + currentName + " has bad number format on line " + lineNumber, e);
					}
				} else {
					throw new Exception("Model " + currentName + " has a bad number of values on line " + lineNumber);
				}
			} else if(currentLine.startsWith("vt ")){
				String[] values = currentLine.split(" ");
				if(values.length == 3){
					try {
						texCoords.add(new Vec2f(Float.parseFloat(values[1]), Float.parseFloat(values[2])));
					} catch(Exception e){
						throw new Exception("Model " + currentName + " has bad number format on line " + lineNumber, e);
					}
				} else {
					throw new Exception("Model " + currentName + " has a bad number of values on line " + lineNumber);
				}
			} else if(currentLine.startsWith("vn ")){
				String[] values = currentLine.split(" ");
				if(values.length == 4){
					try {
						normals.add(new Vec3d(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
					} catch(Exception e){
						throw new Exception("Model " + currentName + " has bad number format on line " + lineNumber, e);
					}
				} else {
					throw new Exception("Model " + currentName + " has a bad number of values on line " + lineNumber);
				}
			} else if(currentLine.startsWith("f ")){
				String[] parts = currentLine.split(" ");
				if(parts.length == 4){
					addVertex(parts[1], lineNumber);
					addVertex(parts[2], lineNumber);
					addVertex(parts[3], lineNumber);
				} else {
					throw new Exception("Model " + currentName + " has bad face format on line " + lineNumber);
				}
			}
			
		}
		if(isDrawing){
			Tessellator.getInstance().draw();
			GL11.glEndList();
		}
		return model;
	}
	
	private static void addVertex(String part, int lineNumber) throws Exception {
		try {
			String[] indices = part.split("/");
			Vec3d pos = vertices.get(Integer.parseInt(indices[0])-1);
			Vec2f tex = texCoords.get(Integer.parseInt(indices[1])-1);
			Vec3d normal = normals.get(Integer.parseInt(indices[2])-1);
			Tessellator.getInstance().getBuffer().pos(pos.x, pos.y, pos.z).tex(tex.x, tex.y).normal((float)normal.x, (float)normal.y, (float)normal.z).endVertex();
		} catch(Exception e){
			throw new Exception("Model " + currentName + " has bad face on line " + lineNumber, e);
		}
	}
}
