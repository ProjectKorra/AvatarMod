package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFlame;
import com.crowsofwar.avatar.common.entity.EntityFireArc.ControlPoint;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderFireArc extends Render {

	@Override
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_,
			float p_76986_9_) {
		
		EntityFireArc flame = (EntityFireArc) p_76986_1_;
		
		for (int i = 0; i < flame.getControlPoints().length; i++) {
			ControlPoint cp = flame.getControlPoint(i);
			String particleName = i == 0 ? "flame" : (i == 1 ? "smoke" : "reddust");
			flame.worldObj.spawnParticle(particleName, cp.getXPos(), cp.getYPos(), cp.getZPos(), 0, 0.05, 0);
		}
		
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		// TODO Auto-generated method stub
		return null;
	}

}
