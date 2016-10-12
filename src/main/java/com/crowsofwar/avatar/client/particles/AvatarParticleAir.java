package com.crowsofwar.avatar.client.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarParticleAir extends AvatarParticle {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/particles/air.png");
	
	private static final ParticleFrame[] FRAMES = new ParticleFrame[7];
	static {
		for (int i = 0; i < FRAMES.length; i++) {
			FRAMES[i] = new ParticleFrame(TEXTURE, 256, i * 32, 0, 32, 32);
		}
	}
	
	/**
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param velX
	 * @param velY
	 * @param velZ
	 */
	public AvatarParticleAir(int particleID, World world, double x, double y, double z, double velX,
			double velY, double velZ, int... parameters) {
		super(world, x, y, z, velX, velY, velZ);
		
		width *= 30;
		height *= 30;
		
		particleMaxAge *= 3;
		
		motionX = velX;
		motionY = velY;
		motionZ = velZ;
		
	}
	
	@Override
	protected ParticleFrame[] getTextureFrames() {
		return FRAMES;
	}
	
}
