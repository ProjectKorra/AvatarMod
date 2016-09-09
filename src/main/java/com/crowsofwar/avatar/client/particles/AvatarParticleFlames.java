package com.crowsofwar.avatar.client.particles;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AvatarParticleFlames extends AvatarParticle {
	
	private static final ParticleFrame[] FRAMES = new ParticleFrame[7];
	static {
		for (int i = 0; i < FRAMES.length; i++) {
			FRAMES[i] = new ParticleFrame(32 * i, 0, 32, 32);
		}
	}
	
	public AvatarParticleFlames(int particleID, World world, double x, double y, double z, double velX,
			double velY, double velZ, int... parameters) {
		
		this(world, x, y, z, velX, velY, velZ);
		
	}
	
	protected AvatarParticleFlames(World world, double x, double y, double z, double velX, double velY,
			double velZ) {
		
		super(world, x, y, z, velX, velY, velZ);
		this.particleRed = 1.0F;
		this.particleGreen = 1.0F;
		this.particleBlue = 1.0F;
		this.setParticleTextureIndex(4);
		this.setSize(0.02F, 0.02F);
		this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
		this.motionX = velX * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
		this.motionY = velY * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
		this.motionZ = velZ * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
		this.particleAge = 0;
		this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
		
		enableAdditiveBlending();
		
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		// this.prevPosX = this.posX;
		// this.prevPosY = this.posY;
		// this.prevPosZ = this.posZ;
		// this.motionY += 0.002D;
		// this.moveEntity(this.motionX, this.motionY, this.motionZ);
		// this.motionX *= 0.8500000238418579D;
		// this.motionY *= 0.8500000238418579D;
		// this.motionZ *= 0.8500000238418579D;
		//
		// if (this.particleAge-- <= 0) {
		// this.setExpired();
		// }
	}
	
	@Override
	protected ParticleFrame[] getTextureFrames() {
		return FRAMES;
	}
	
}