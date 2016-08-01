package com.crowsofwar.avatar.client.render;

import java.util.Random;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderAirGust extends Render {
	
	private static final Random random = new Random();
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
		
		World world = entity.worldObj;
		AxisAlignedBB boundingBox = entity.getBoundingBox();
		double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
		double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
		double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
		world.spawnParticle("cloud", spawnX, spawnY, spawnZ, 0, 0, 0);
		
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

}
