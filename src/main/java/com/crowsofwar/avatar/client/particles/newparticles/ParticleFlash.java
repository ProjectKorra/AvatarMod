package com.crowsofwar.avatar.client.particles.newparticles;

import com.crowsofwar.avatar.common.bending.fire.Firebending;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static org.lwjgl.opengl.GL11.GL_LESS;

/**
 * Copied from ParticleFirework.Overlay; for some reason that class has no public constructors, plus I want to change the
 * scale and a few other things
 *
 * @author Electroblob, FavouriteDragon
 * @since Wizardry 4.2.0, Av2 1.6.0
 */
@SideOnly(Side.CLIENT)
public class ParticleFlash extends ParticleAvatar {

	public ParticleFlash(World world, double x, double y, double z) {
		super(world, x, y, z);
		this.setRBGColorF(1, 1, 1);
		this.setAlphaF(1.0F);
		this.particleScale = 0.6f; // 7.1f is the value used in fireworks
		this.particleMaxAge = 6;
	}


	@Override
	public boolean shouldDisableDepth() {
		return true; // Well this fixes everything... let's hope it doesn't cause any side-effects!
	}

	@Override
	public int getFXLayer() {
		return 0;
	}

	@Override
	public void drawParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float f4;
		if (CLIENT_CONFIG.particleSettings.voxelFlashParticles || CLIENT_CONFIG.particleSettings.squareFlashParticles) {
			f4 = particleScale * 0.725F * MathHelper.sin(((float) this.particleAge + partialTicks - 1.0F) / particleMaxAge * (float) Math.PI);
		}
		else {
			f4 = particleScale * MathHelper.sin(((float) this.particleAge + partialTicks - 1.0F) / particleMaxAge * (float) Math.PI);
		}
		//Great for fire!
		if (element instanceof Firebending)
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		this.setAlphaF(0.6F - ((float) this.particleAge + partialTicks - 1.0F) / particleMaxAge * 0.5F);
		float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		GlStateManager.pushMatrix();
		if (CLIENT_CONFIG.particleSettings.voxelFlashParticles) {
		//	GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.SRC_ALPHA);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_CONSTANT_ALPHA, GlStateManager.DestFactor.ONE);
		}
		//TODO: Figure out a way to do this without breaking everything else
		if (CLIENT_CONFIG.particleSettings.squareFlashParticles) {
			GlStateManager.disableTexture2D();
			GlStateManager.disableNormalize();
		}

		GlStateManager.enableDepth();
		//This does some cool stuff:
	//	GlStateManager.depthMask(true);
		buffer.pos(f5 - rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 - rotationYZ * f4 - rotationXZ * f4).tex(0.5D, 0.375D)
				.color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		buffer.pos(f5 - rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 - rotationYZ * f4 + rotationXZ * f4).tex(0.5D, 0.125D)
				.color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		buffer.pos(f5 + rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 + rotationYZ * f4 + rotationXZ * f4).tex(0.25D, 0.125D)
				.color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		buffer.pos(f5 + rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 + rotationYZ * f4 - rotationXZ * f4).tex(0.25D, 0.375D)
				.color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		GlStateManager.popMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.enableNormalize();
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 15728880;
	}
}
