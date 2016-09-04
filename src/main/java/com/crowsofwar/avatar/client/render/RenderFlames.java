package com.crowsofwar.avatar.client.render;

import java.util.Random;

import com.crowsofwar.avatar.common.entity.EntityFlames;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class RenderFlames extends Render<EntityFlames> {
	
	private final Random random;
	
	/**
	 * @param renderManager
	 */
	public RenderFlames(RenderManager renderManager) {
		super(renderManager);
		this.random = new Random();
	}
	
	@Override
	public void doRender(EntityFlames entity, double x, double y, double z, float entityYaw,
			float partialTicks) {
		
		entity.worldObj.spawnParticle(EnumParticleTypes.FLAME, entity.posX, entity.posY, entity.posZ,
				(random.nextGaussian() - 0.5) * 0.02, random.nextGaussian() * 0.01,
				(random.nextGaussian() - 0.5) * 0.02);
		
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityFlames entity) {
		return null;
	}
	
}
