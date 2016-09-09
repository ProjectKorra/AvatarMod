package com.crowsofwar.avatar.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AvatarParticle extends Particle {
	
	private static final ResourceLocation VANILLA_PARTICLES = new ResourceLocation(
			"textures/particle/particles.png");
	private static final ResourceLocation AVATAR_PARTICLES = new ResourceLocation("avatarmod",
			"textures/particles/flame.png");
	
	private boolean additiveBlending;
	
	protected AvatarParticle(World world, double x, double y, double z, double velX, double velY,
			double velZ) {
		
		super(world, x, y, z, velX, velY, velZ);
		
		this.additiveBlending = false;
		
	}
	
	@Override
	public void renderParticle(VertexBuffer vb, Entity entity, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		
		ParticleFrame frame = getTextureFrames()[getCurrentFrame()];
		
		Tessellator t = Tessellator.getInstance();
		Minecraft mc = Minecraft.getMinecraft();
		
		t.draw();
		mc.getTextureManager().bindTexture(frame.texture);
		vb.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		if (additiveBlending) {
			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
		}
		
		// CUSTOM RENDER PARTICLE
		
		float minU = (float) frame.minU / frame.textureSize;
		float maxU = (float) frame.maxU / frame.textureSize;
		float minV = (float) frame.minV / frame.textureSize;
		float maxV = (float) frame.maxV / frame.textureSize;
		float f4 = 0.1F * this.particleScale;
		
		float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		Vec3d[] avec3d = new Vec3d[] {
				new Vec3d((double) (-rotationX * f4 - rotationXY * f4), (double) (-rotationZ * f4),
						(double) (-rotationYZ * f4 - rotationXZ * f4)),
				new Vec3d((double) (-rotationX * f4 + rotationXY * f4), (double) (rotationZ * f4),
						(double) (-rotationYZ * f4 + rotationXZ * f4)),
				new Vec3d((double) (rotationX * f4 + rotationXY * f4), (double) (rotationZ * f4),
						(double) (rotationYZ * f4 + rotationXZ * f4)),
				new Vec3d((double) (rotationX * f4 - rotationXY * f4), (double) (-rotationZ * f4),
						(double) (rotationYZ * f4 - rotationXZ * f4)) };
		
		if (this.field_190014_F != 0.0F) {
			float f8 = this.field_190014_F + (this.field_190014_F - this.field_190015_G) * partialTicks;
			float f9 = MathHelper.cos(f8 * 0.5F);
			float f10 = MathHelper.sin(f8 * 0.5F) * (float) field_190016_K.xCoord;
			float f11 = MathHelper.sin(f8 * 0.5F) * (float) field_190016_K.yCoord;
			float f12 = MathHelper.sin(f8 * 0.5F) * (float) field_190016_K.zCoord;
			Vec3d vec3d = new Vec3d((double) f10, (double) f11, (double) f12);
			
			for (int l = 0; l < 4; ++l) {
				avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d))
						.add(avec3d[l].scale((double) (f9 * f9) - vec3d.dotProduct(vec3d)))
						.add(vec3d.crossProduct(avec3d[l]).scale((double) (2.0F * f9)));
			}
		}
		
		vb.pos((double) f5 + avec3d[0].xCoord, (double) f6 + avec3d[0].yCoord, (double) f7 + avec3d[0].zCoord)
				.tex((double) maxU, (double) maxV)
				.color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
				.lightmap(j, k).endVertex();
		vb.pos((double) f5 + avec3d[1].xCoord, (double) f6 + avec3d[1].yCoord, (double) f7 + avec3d[1].zCoord)
				.tex((double) maxU, (double) minV)
				.color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
				.lightmap(j, k).endVertex();
		vb.pos((double) f5 + avec3d[2].xCoord, (double) f6 + avec3d[2].yCoord, (double) f7 + avec3d[2].zCoord)
				.tex((double) minU, (double) minV)
				.color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
				.lightmap(j, k).endVertex();
		vb.pos((double) f5 + avec3d[3].xCoord, (double) f6 + avec3d[3].yCoord, (double) f7 + avec3d[3].zCoord)
				.tex((double) minU, (double) maxV)
				.color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
				.lightmap(j, k).endVertex();
		
		// CUSTOM RENDER PARTICLE STOP
		
		t.draw();
		if (additiveBlending) {
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		}
		mc.getTextureManager().bindTexture(VANILLA_PARTICLES);
		vb.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		
	}
	
	/**
	 * Enable additive blending for a "glowing" effect when there are many particles.
	 */
	protected void enableAdditiveBlending() {
		additiveBlending = true;
	}
	
	protected abstract ParticleFrame[] getTextureFrames();
	
	protected int getCurrentFrame() {
		int frame = (int) ((double) (particleAge * getTextureFrames().length) / particleMaxAge);
		if (frame == getTextureFrames().length) frame = getTextureFrames().length - 1;
		return frame;
	}
	
	public static class ParticleFrame {
		
		private final ResourceLocation texture;
		private final int minU, maxU, minV, maxV, textureSize;
		
		/**
		 * Create a particle frame.
		 * 
		 * @param texture
		 *            The texture location
		 * @param textureSize
		 *            Size of your texture (width/height) in pixels. Texture is assumed to be
		 *            square, so 64 = 64x64 pixels, etc.
		 * @param minU
		 *            Minimum x-coordinate on texture
		 * @param minV
		 *            Minimum y-coordinate on texture
		 * @param width
		 *            Width in pixels of texture
		 * @param height
		 *            Height in pixels of texture
		 */
		public ParticleFrame(ResourceLocation texture, int textureSize, int minU, int minV, int width,
				int height) {
			this.texture = texture;
			this.textureSize = textureSize;
			this.minU = minU;
			this.maxU = minU + width;
			this.minV = minV;
			this.maxV = minV + height;
		}
		
		/**
		 * Create a particle frame using the Avatar spritesheet.
		 * 
		 * @param minU
		 *            Minimum x-coordinate on texture
		 * @param minV
		 *            Minimum y-coordinate on texture
		 * @param width
		 *            Width in pixels of texture
		 * @param height
		 *            Height in pixels of texture
		 */
		public ParticleFrame(int minU, int minV, int width, int height) {
			this(AVATAR_PARTICLES, 256, minU, minV, width, height);
		}
		
	}
	
}