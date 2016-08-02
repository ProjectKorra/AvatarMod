package com.crowsofwar.avatar.client.render;

import java.util.Random;

import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderAirGust extends RenderArc {
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod", "textures/entity/air-ribbon.png");
	
	private static final Random random = new Random();
	
	@Override
	protected void onDrawSegment(EntityArc arc, EntityControlPoint first, EntityControlPoint second) {
		
		World world = arc.worldObj;
		AxisAlignedBB boundingBox = first.boundingBox;
		double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
		double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
		double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
		world.spawnParticle("cloud", spawnX, spawnY, spawnZ, 0, 0, 0);
		
	}
	
	@Override
	protected ResourceLocation getTexture() {
		return TEXTURE;
	}
	
}
