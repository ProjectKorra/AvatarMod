package com.crowsofwar.avatar.client.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AvatarParticleHealing extends AvatarParticle {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/particles/healing.png");

	private static final ParticleFrame[] FRAMES = new ParticleFrame[8];

	static {
		for (int i = 0; i < FRAMES.length; i++) {
			FRAMES[i] = new ParticleFrame(TEXTURE, 256, (i % 4) * 64, i / 64, 64, 64);
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
	public AvatarParticleHealing(int particleID, World world, double x, double y, double z, double velX,
							 double velY, double velZ, int... parameters) {
		super(world, x, y, z, velX, velY, velZ);

		particleScale = 4f;
		particleMaxAge *= 2;

		motionX = velX;
		motionY = velY;
		motionZ = velZ;

	}

	@Override
	protected ParticleFrame[] getTextureFrames() {
		return FRAMES;
	}

}
