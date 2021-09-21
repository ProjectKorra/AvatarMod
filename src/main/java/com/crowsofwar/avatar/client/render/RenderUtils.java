package com.crowsofwar.avatar.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

/**
 * @author CrowsOfWar
 */
public class RenderUtils {

	public static void drawQuad(int normal, Vector4f pos1, Vector4f pos2, Vector4f pos3, Vector4f
			pos4, double u1, double v1, double u2, double v2) {

		Tessellator t = Tessellator.getInstance();
		BufferBuilder vb = t.getBuffer();

		if (normal == 0 || normal == 2) {
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).endVertex();
			vb.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).endVertex();
			vb.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).endVertex();
			vb.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).endVertex();
			t.draw();
		}
		if (normal == 1 || normal == 2) {
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).endVertex();
			vb.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).endVertex();
			vb.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).endVertex();
			vb.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).endVertex();
			t.draw();
		}
	}

	public static void renderCube(float x, float y, float z, double u1, double u2, double v1, double v2, float size,
							float rotateX, float rotateY, float rotateZ) {
		Matrix4f mat = new Matrix4f();
		mat.translate(x, y + .4f, z);

		mat.rotate(rotateX, 1, 0, 0);
		mat.rotate(rotateY, 0, 1, 0);
		mat.rotate(rotateZ, 0, 0, 1);

		// @formatter:off
		// Can't use .mul(size) here because it would mul the w component
		Vector4f
				lbf = new Vector4f(-.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				rbf = new Vector4f(0.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				ltf = new Vector4f(-.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				rtf = new Vector4f(0.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				lbb = new Vector4f(-.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				rbb = new Vector4f(0.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				ltb = new Vector4f(-.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat),
				rtb = new Vector4f(0.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat);

		// @formatter:on

		drawQuad(2, ltb, lbb, lbf, ltf, u1, v1, u2, v2); // -x
		drawQuad(2, rtb, rbb, rbf, rtf, u1, v1, u2, v2); // +x
		drawQuad(2, rbb, rbf, lbf, lbb, u1, v1, u2, v2); // -y
		drawQuad(2, rtb, rtf, ltf, ltb, u1, v1, u2, v2); // +y
		drawQuad(2, rtf, rbf, lbf, ltf, u1, v1, u2, v2); // -z
		drawQuad(2, rtb, rbb, lbb, ltb, u1, v1, u2, v2); // +z
	}

	/**
	 * Draws a sphere (using lat/long triangles) with the given parameters.
	 *
	 * @param radius   The radius of the sphere.
	 * @param latStep  The latitude step; smaller is smoother but increases performance cost.
	 * @param longStep The longitude step; smaller is smoother but increases performance cost.
	 * @param inside   Whether to draw the outside or the inside of the sphere.
	 * @param r        The red component of the sphere colour.
	 * @param g        The green component of the sphere colour.
	 * @param b        The blue component of the sphere colour.
	 * @param a        The alpha component of the sphere colour.
	 */
	public static void drawSphere(float radius, float latStep, float longStep, boolean inside, float r, float g, float b, float a) {

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		//Need to change this so it supports textures
		buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

		boolean goingUp = inside;

		buffer.pos(0, goingUp ? -radius : radius, 0).color(r, g, b, a).endVertex(); // Start at the north pole

		for (float longitude = -(float) Math.PI; longitude <= (float) Math.PI; longitude += longStep) {

			// Leave the poles out since they only have a single point per stack instead of two
			for (float theta = (float) Math.PI / 2 - latStep; theta >= -(float) Math.PI / 2 + latStep; theta -= latStep) {

				float latitude = goingUp ? -theta : theta;

				float hRadius = radius * MathHelper.cos(latitude);
				float vy = radius * MathHelper.sin(latitude);
				float vx = hRadius * MathHelper.sin(longitude);
				float vz = hRadius * MathHelper.cos(longitude);
				buffer.pos(vx, vy, vz).color(r, g, b, a).endVertex();
				vx = hRadius * MathHelper.sin(longitude + longStep);
				vz = hRadius * MathHelper.cos(longitude + longStep);


				buffer.pos(vx, vy, vz).color(r, g, b, a).endVertex();

			}


			buffer.pos(0, goingUp ? radius : -radius, 0).color(r, g, b, a).endVertex();

			goingUp = !goingUp;
		}

		tessellator.draw();
	}
}
