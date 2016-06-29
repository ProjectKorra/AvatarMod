package com.crowsofwar.avatar.client;

import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFireArc.ControlPoint;
import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderFireArc extends Render {

	private static final ResourceLocation fire = new ResourceLocation("avatarmod", "textures/entity/fire.png");
	
	@Override
	public void doRender(Entity p_76986_1_, double xx, double yy, double zz, float p_76986_8_,
			float p_76986_9_) {
		ResourceLocation fire = new ResourceLocation("avatarmod", "textures/entity/fire.png");
		
		EntityFireArc flame = (EntityFireArc) p_76986_1_;
		{
			ControlPoint cp = flame.getControlPoint(0);
			flame.worldObj.spawnParticle("flame", cp.getXPos(), cp.getYPos(), cp.getZPos(), 0, 0.05, 0);
		}
		
		for (int i = 1; i < flame.getControlPoints().length; i++) {
			ControlPoint cp = flame.getControlPoint(i);
			ControlPoint leader = flame.getControlPoint(i - 1);
			
			double x = leader.getXPos() - renderManager.renderPosX;
			double y = leader.getYPos() - renderManager.renderPosY;
			double z = leader.getZPos() - renderManager.renderPosZ;
			
			String particleName = i == 0 ? "flame" : (i == 1 ? "smoke" : "reddust");
			flame.worldObj.spawnParticle(particleName, cp.getXPos(), cp.getYPos(), cp.getZPos(), 0, 0.05, 0);
			
//			if (i != 1) continue;
			
			Vec3 from = flame.getControlPoint(i - 1).getPos();
			Vec3 to = cp.getPos();
			
			Vec3 diff = VectorUtils.minus(from, to);
			
			double ySize = 1;
			int textureRepeat = 2;
			
			Minecraft.getMinecraft().renderEngine.bindTexture(fire);
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GL11.glDisable(GL11.GL_LIGHTING);
			Tessellator t = Tessellator.instance;
			t.startDrawingQuads();
			t.addVertexWithUV(0, 0, 0, textureRepeat, 0); // 1
			t.addVertexWithUV(0, -1, 0, textureRepeat, textureRepeat); // 2
			t.addVertexWithUV(-diff.xCoord, -diff.yCoord - ySize * .5, -diff.zCoord, 0, textureRepeat); // 3
			t.addVertexWithUV(-diff.xCoord, ySize * .5, -diff.zCoord, 0, 0); // 4
			t.draw();
			t.startDrawingQuads();
			t.addVertexWithUV(0, 0, 0, textureRepeat, 0);//1
			t.addVertexWithUV(-diff.xCoord, ySize * .5, -diff.zCoord, 0, 0);//4
			t.addVertexWithUV(-diff.xCoord, -diff.yCoord - ySize * .5, -diff.zCoord, 0, textureRepeat);//3
			t.addVertexWithUV(0, -1, 0, textureRepeat, textureRepeat);//2
			t.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
			
			
		}
		
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		// TODO Auto-generated method stub
		return null;
	}

}
