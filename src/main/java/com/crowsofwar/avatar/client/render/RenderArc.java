package com.crowsofwar.avatar.client.render;

import static com.crowsofwar.avatar.common.util.VectorUtils.copy;
import static com.crowsofwar.avatar.common.util.VectorUtils.fromYawPitch;
import static com.crowsofwar.avatar.common.util.VectorUtils.getRotations;
import static com.crowsofwar.avatar.common.util.VectorUtils.minus;
import static com.crowsofwar.avatar.common.util.VectorUtils.plus;
import static com.crowsofwar.avatar.common.util.VectorUtils.times;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public abstract class RenderArc extends Render {

	@Override
	public final void doRender(Entity p_76986_1_, double xx, double yy, double zz, float p_76986_8_,
			float p_76986_9_) {
		
		EntityArc arc = (EntityArc) p_76986_1_;
		{
//			ControlPoint cp = flame.getControlPoint(0);
//			flame.worldObj.spawnParticle("flame", cp.getXPos(), cp.getYPos(), cp.getZPos(), 0, 0.05, 0);
		}
		
		for (int i = 1; i < arc.getControlPoints().length; i++) {
			renderSegment(arc, arc.getLeader(i), arc.getControlPoint(i));
		}
		
	}

	private void renderSegment(EntityArc arc, EntityControlPoint leader, EntityControlPoint point) {
		double x = leader.getXPos() - renderManager.renderPosX;
		double y = leader.getYPos() - renderManager.renderPosY;
		double z = leader.getZPos() - renderManager.renderPosZ;
		
		Vec3 from = vec3(0, 0, 0);
		Vec3 to = minus(point.getPosition(), leader.getPosition());
		
		Vec3 diff = minus(to, from);
		
		double ySize = 1;
		int textureRepeat = 2;
		
		Minecraft.getMinecraft().renderEngine.bindTexture(getTexture());
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

//		double size = arc.width / 2;
		double sizeLeader = point.width / 2;
		double sizePoint = leader.width / 2;
		
		Vec3 lookingEuler = getRotations(from, to);
		// Offset for rotated positive X
		Vec3 offX = times(fromYawPitch(lookingEuler.yCoord + Math.toRadians(90), lookingEuler.xCoord), sizeLeader);
		offX.yCoord = 0;
		Vec3 invX = times(offX, -1);
		
//		Matrix4d mat = new Matrix4d();
//		mat.translate(leader.getXPos(), leader.getYPos(), leader.getZPos());
//		mat.rotate(Math.toRadians(110), new Vector3f(0, 1, 0));
//		mat.rotate(Math.toRadians(-45), new Vector3f(1, 0, 0));
//		Vector4d dest = new Vector4d(0, 0, 1, 1).mul(mat);
//		if (arc.getControlPoint(0) == leader)
//			leader.worldObj.spawnParticle("cloud", dest.x, dest.y, dest.z, 0, 0, 0);
		
		double u1 = ((arc.ticksExisted / 20.0) % 1);
		double u2 = (u1 + 0.5);
		
		GL11.glColor3f(1, 1, 1);
		
		// Make 'back' matrix, face it forward
		Matrix4d mat = new Matrix4d();
		mat.rotate(-lookingEuler.yCoord, 0, 1, 0);
		mat.rotate(lookingEuler.xCoord, 1, 0, 0);
		double dist = leader.getDistance(point);
		
		
		Vector4d vert1 = new Vector4d(-sizeLeader, 0, dist, 1).mul(mat);
		Vector4d vert2 = new Vector4d(sizeLeader, 0, dist, 1).mul(mat);
		Vector4d vert3 = new Vector4d(sizePoint, 0, 0, 1).mul(mat);
		Vector4d vert4 = new Vector4d(-sizePoint, 0, 0, 1).mul(mat);
		
		Vector4d debug = vert4;
		arc.worldObj.spawnParticle("spell", debug.x, debug.y, debug.z, 0, 0, 0);
		
		drawQuad(2, vert1, vert2, vert3, vert4, u1, 0, u2, 1);
		
//		drawQuad(2, plus(vec3(to, 0, -sizeLeader, 0), offX), plus(vec3(to, 0, sizeLeader, 0), offX), plus(vec3(from, 0, sizePoint, 0), offX), plus(vec3(from, 0, -sizePoint, 0), offX), u1, 0, u2, 1);
//		// -x side (WEST)
//		drawQuad(2, plus(vec3(to, 0, -sizeLeader, 0), invX), plus(vec3(to, 0, sizeLeader, 0), invX), plus(vec3(from, 0, sizePoint, 0), invX), plus(vec3(from, 0, -sizePoint, 0), invX), u1, 0, u2, 1);
//		// +y
//		drawQuad(2, plus(vec3(to, 0, sizeLeader, 0), offX), plus(vec3(to, 0, sizeLeader, 0), invX), plus(vec3(from, 0, sizePoint, 0), invX), plus(vec3(from, 0, sizePoint, 0), offX), u1, 0, u2, 1);
//		// -y
//		drawQuad(2, plus(vec3(to, 0, -sizeLeader, 0), offX), plus(vec3(to, 0, -sizeLeader, 0), invX), plus(vec3(from, 0, -sizePoint, 0), invX), plus(vec3(from, 0, -sizePoint, 0), offX), u1, 0, u2, 1);
		
		// +x side (EAST)
//		drawQuad(0, plus(vec3(to, 0, -sizeLeader, 0), offX), plus(vec3(to, 0, sizeLeader, 0), offX), plus(vec3(from, 0, sizePoint, 0), offX), plus(vec3(from, 0, -sizePoint, 0), offX), u1, 0, u2, 1);
//		// -x side (WEST)
//		drawQuad(1, plus(vec3(to, 0, -sizeLeader, 0), invX), plus(vec3(to, 0, sizeLeader, 0), invX), plus(vec3(from, 0, sizePoint, 0), invX), plus(vec3(from, 0, -sizePoint, 0), invX), u1, 0, u2, 1);
//		// +z side (SOUTH)
////		drawQuad(vec3(from, 0, size, size), vec3(from, 0, -size, size), vec3(to, 0, -size, size), vec3(to, 0, size, size), 0, 0, 1, 1);
//		// +y
//		drawQuad(0, plus(vec3(to, 0, sizeLeader, 0), offX), plus(vec3(to, 0, sizeLeader, 0), invX), plus(vec3(from, 0, sizePoint, 0), invX), plus(vec3(from, 0, sizePoint, 0), offX), u1, 0, u2, 1);
//		// -y
//		drawQuad(1, plus(vec3(to, 0, -sizeLeader, 0), offX), plus(vec3(to, 0, -sizeLeader, 0), invX), plus(vec3(from, 0, -sizePoint, 0), invX), plus(vec3(from, 0, -sizePoint, 0), offX), u1, 0, u2, 1);
		
		onDrawSegment(arc, leader, point);
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	
	@Override
	protected final ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}
	
	private void drawQuad(int normal, Vec3 pos1, Vec3 pos2, Vec3 pos3, Vec3 pos4, double u1, double v1, double u2, double v2) {
		Tessellator t = Tessellator.instance;
		if (normal == 0 || normal == 2) {
			t.startDrawingQuads();
			t.addVertexWithUV(pos1.xCoord, pos1.yCoord, pos1.zCoord, u2, v1); // 1
			t.addVertexWithUV(pos2.xCoord, pos2.yCoord, pos2.zCoord, u2, v2); // 2
			t.addVertexWithUV(pos3.xCoord, pos3.yCoord, pos3.zCoord, u1, v2); // 3
			t.addVertexWithUV(pos4.xCoord, pos4.yCoord, pos4.zCoord, u1, v1); // 4
			t.draw();
		}
		if (normal == 1 || normal == 2) {
			t.startDrawingQuads();
			t.addVertexWithUV(pos1.xCoord, pos1.yCoord, pos1.zCoord, u2, v1); // 1
			t.addVertexWithUV(pos4.xCoord, pos4.yCoord, pos4.zCoord, u1, v1); // 4
			t.addVertexWithUV(pos3.xCoord, pos3.yCoord, pos3.zCoord, u1, v2); // 3
			t.addVertexWithUV(pos2.xCoord, pos2.yCoord, pos2.zCoord, u2, v2); // 2
			t.draw();
		}
	}
	
	private void drawQuad(int normal, Vector4d pos1, Vector4d pos2, Vector4d pos3, Vector4d pos4, double u1, double v1, double u2, double v2) {
		drawQuad(normal,
				Vec3.createVectorHelper(pos1.x, pos1.y, pos1.z),
				Vec3.createVectorHelper(pos2.x, pos2.y, pos2.z),
				Vec3.createVectorHelper(pos3.x, pos3.y, pos3.z),
				Vec3.createVectorHelper(pos4.x, pos4.y, pos4.z),
				u1, v1, u2, v2);
	}
	
	private Vec3 vec3(double x, double y, double z) {
		return Vec3.createVectorHelper(x, y, z);
	}
	
	private Vec3 vec3(Vec3 vec, double x, double y, double z) {
		return copy(vec).addVector(x, y, z);
	}
	
	protected abstract ResourceLocation getTexture();

	protected void onDrawSegment(EntityArc arc, EntityControlPoint first, EntityControlPoint second) {
		
	}
	
}
