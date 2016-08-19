package com.crowsofwar.avatar.client.render;

import static com.crowsofwar.avatar.common.util.VectorUtils.*;

import org.joml.Matrix4d;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public abstract class RenderArc extends Render {
	
	private final RenderManager renderManager;
	
	/**
	 * @param renderManager
	 */
	protected RenderArc(RenderManager renderManager) {
		super(renderManager);
		this.renderManager = renderManager;
	}
	
	/**
	 * Whether to render with full brightness.
	 */
	private boolean renderBright;
	
	@Override
	public final void doRender(Entity entity, double xx, double yy, double zz, float p_76986_8_,
			float partialTicks) {
		
		double renderPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		double renderPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		double renderPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
		
		EntityArc arc = (EntityArc) entity;
		
		for (int i = 1; i < arc.getControlPoints().length; i++) {
			renderSegment(arc, arc.getLeader(i), arc.getControlPoint(i), renderPosX, renderPosY, renderPosZ);
		}
		
	}
	
	private void renderSegment(EntityArc arc, EntityControlPoint leader, EntityControlPoint point,
			double renderPosX, double renderPosY, double renderPosZ) {
		double x = leader.getXPos() - renderPosX;
		double y = leader.getYPos() - renderPosY;
		double z = leader.getZPos() - renderPosZ;
		
		Vec3d from = Vec3d(0, 0, 0);
		Vec3d to = minus(point.getPosition(), leader.getPosition());
		
		Vec3d diff = minus(to, from);
		
		double ySize = 1;
		int textureRepeat = 2;
		
		Minecraft.getMinecraft().renderEngine.bindTexture(getTexture());
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		if (renderBright) GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		// double size = arc.width / 2;
		double sizeLeader = point.width / 2;
		double sizePoint = leader.width / 2;
		
		Vec3d lookingEuler = getRotations(from, to);
		// Offset for rotated positive X
		Vec3d offX = times(fromYawPitch(lookingEuler.yCoord + Math.toRadians(90), lookingEuler.xCoord),
				sizeLeader);
		offX.yCoord = 0;
		Vec3d invX = times(offX, -1);
		
		// Matrix4d mat = new Matrix4d();
		// mat.translate(leader.getXPos(), leader.getYPos(), leader.getZPos());
		// mat.rotate(Math.toRadians(110), new Vector3f(0, 1, 0));
		// mat.rotate(Math.toRadians(-45), new Vector3f(1, 0, 0));
		// Vector4d dest = new Vector4d(0, 0, 1, 1).mul(mat);
		// if (arc.getControlPoint(0) == leader)
		// leader.worldObj.spawnParticle("cloud", dest.x, dest.y, dest.z, 0, 0, 0);
		
		double u1 = ((arc.ticksExisted / 20.0) % 1);
		double u2 = (u1 + 0.5);
		
		GL11.glColor3f(1, 1, 1);
		
		// Make 'back' matrix, face it forward
		Matrix4d mat = new Matrix4d();
		mat.rotate((float) -lookingEuler.yCoord, 0, 1, 0);
		mat.rotate((float) lookingEuler.xCoord, 1, 0, 0);
		double dist = leader.getDistance(point);
		
		Vector4d t_v1 = new Vector4d(-sizeLeader, sizeLeader, dist, 1).mul(mat);
		Vector4d t_v2 = new Vector4d(sizeLeader, sizeLeader, dist, 1).mul(mat);
		Vector4d t_v3 = new Vector4d(sizePoint, sizePoint, 0, 1).mul(mat);
		Vector4d t_v4 = new Vector4d(-sizePoint, sizePoint, 0, 1).mul(mat);
		
		Vector4d b_v1 = new Vector4d(-sizeLeader, -sizeLeader, dist, 1).mul(mat);
		Vector4d b_v2 = new Vector4d(sizeLeader, -sizeLeader, dist, 1).mul(mat);
		Vector4d b_v3 = new Vector4d(sizePoint, -sizePoint, 0, 1).mul(mat);
		Vector4d b_v4 = new Vector4d(-sizePoint, -sizePoint, 0, 1).mul(mat);
		
		// Draw top segment
		drawQuad(2, t_v1, t_v2, t_v3, t_v4, u1, 0, u2, 1);
		// Draw bottom segment
		drawQuad(2, b_v1, b_v2, b_v3, b_v4, u1, 0, u2, 1);
		// Draw right segment(+x)
		drawQuad(2, t_v2, b_v2, b_v3, t_v3, u1, 0, u2, 1);
		// Draw left segment(-x)
		drawQuad(2, t_v1, b_v1, b_v4, t_v4, u1, 0, u2, 1);
		
		onDrawSegment(arc, leader, point);
		
		GL11.glDisable(GL11.GL_BLEND);
		if (renderBright) GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	
	@Override
	protected final ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}
	
	private void drawQuad(int normal, Vec3d pos1, Vec3d pos2, Vec3d pos3, Vec3d pos4, double u1, double v1,
			double u2, double v2) {
		
		Tessellator t = Tessellator.getInstance();
		VertexBuffer vb = t.getBuffer();
		
		if (normal == 0 || normal == 2) {
			
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.xCoord, pos1.yCoord, pos1.zCoord).tex(u2, v1);
			vb.pos(pos2.xCoord, pos2.yCoord, pos2.zCoord).tex(u2, v2);
			vb.pos(pos3.xCoord, pos3.yCoord, pos3.zCoord).tex(u1, v2);
			vb.pos(pos4.xCoord, pos4.yCoord, pos4.zCoord).tex(u1, v1);
			t.draw();
			
			// t.startDrawingQuads();
			// t.addVertexWithUV(pos1.xCoord, pos1.yCoord, pos1.zCoord, u2, v1); // 1
			// t.addVertexWithUV(pos2.xCoord, pos2.yCoord, pos2.zCoord, u2, v2); // 2
			// t.addVertexWithUV(pos3.xCoord, pos3.yCoord, pos3.zCoord, u1, v2); // 3
			// t.addVertexWithUV(pos4.xCoord, pos4.yCoord, pos4.zCoord, u1, v1); // 4
			// t.draw();
		}
		if (normal == 1 || normal == 2) {
			
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.xCoord, pos1.yCoord, pos1.zCoord).tex(u2, v1);
			vb.pos(pos4.xCoord, pos4.yCoord, pos4.zCoord).tex(u1, v1);
			vb.pos(pos3.xCoord, pos3.yCoord, pos3.zCoord).tex(u1, v2);
			vb.pos(pos2.xCoord, pos2.yCoord, pos2.zCoord).tex(u2, v2);
			t.draw();
			
			// t.startDrawingQuads();
			// t.addVertexWithUV(pos1.xCoord, pos1.yCoord, pos1.zCoord, u2, v1); // 1
			// t.addVertexWithUV(pos4.xCoord, pos4.yCoord, pos4.zCoord, u1, v1); // 4
			// t.addVertexWithUV(pos3.xCoord, pos3.yCoord, pos3.zCoord, u1, v2); // 3
			// t.addVertexWithUV(pos2.xCoord, pos2.yCoord, pos2.zCoord, u2, v2); // 2
			// t.draw();
		}
	}
	
	private void drawQuad(int normal, Vector4d pos1, Vector4d pos2, Vector4d pos3, Vector4d pos4, double u1,
			double v1, double u2, double v2) {
		drawQuad(normal, Vec3d.createVectorHelper(pos1.x, pos1.y, pos1.z),
				Vec3d.createVectorHelper(pos2.x, pos2.y, pos2.z),
				Vec3d.createVectorHelper(pos3.x, pos3.y, pos3.z),
				Vec3d.createVectorHelper(pos4.x, pos4.y, pos4.z), u1, v1, u2, v2);
	}
	
	private Vec3d Vec3d(double x, double y, double z) {
		return Vec3d.createVectorHelper(x, y, z);
	}
	
	private Vec3d Vec3d(Vec3d vec, double x, double y, double z) {
		return copy(vec).addVector(x, y, z);
	}
	
	protected abstract ResourceLocation getTexture();
	
	protected void onDrawSegment(EntityArc arc, EntityControlPoint first, EntityControlPoint second) {
		
	}
	
	/**
	 * Render this arc to disregard the actual lighting and instead render with full brightness
	 */
	protected void enableFullBrightness() {
		renderBright = true;
	}
	
}
