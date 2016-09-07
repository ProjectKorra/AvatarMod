package com.crowsofwar.avatar.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
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
		this.setParticleTexture(getTextureFrames()[0]);
		
	}
	
	@Override
	public void renderParticle(VertexBuffer vb, Entity entity, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		
		setParticleTexture(getTextureFrames()[getCurrentFrame()]);
		
		Tessellator t = Tessellator.getInstance();
		Minecraft mc = Minecraft.getMinecraft();
		
		t.draw();
		mc.getTextureManager().bindTexture(AVATAR_PARTICLES);
		vb.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		if (additiveBlending) {
			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
		}
		
		super.renderParticle(vb, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY,
				rotationXZ);
		
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
	
	protected abstract TextureAtlasSprite[] getTextureFrames();
	
	protected int getCurrentFrame() {
		return (int) (((double) particleAge / particleMaxAge) * getTextureFrames().length);
	}
	
}