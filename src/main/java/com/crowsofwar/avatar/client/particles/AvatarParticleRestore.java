package com.crowsofwar.avatar.client.particles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AvatarParticleRestore extends AvatarParticle {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod", "textures/particles/restore.png");

	private static final ParticleFrame[] FRAMES = new ParticleFrame[6];

	static {
		for (int i = 0; i < FRAMES.length; i++) {
			FRAMES[i] = new ParticleFrame(TEXTURE, 256, (i % 4) * 64, i / 64, 64, 64);
		}
	}

	public AvatarParticleRestore(int particleID, World world, double x, double y, double z, double velX, double velY, double velZ,
					int... parameters) {
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
