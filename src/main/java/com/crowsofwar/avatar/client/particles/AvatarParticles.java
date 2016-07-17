package com.crowsofwar.avatar.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class AvatarParticles {
	
	public static void createParticle(World world, double x, double y, double z, double velX, double velY, double velZ) {
		Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTest(world, x, y, z, velX, velY, velZ));
	}
	
}
