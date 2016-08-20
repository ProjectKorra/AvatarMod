// package com.crowsofwar.avatar.client.particles;
//
// import org.lwjgl.opengl.GlStateManager;
//
// import net.minecraft.client.Minecraft;
// import net.minecraft.client.renderer.Tessellator;
// import net.minecraft.util.ResourceLocation;
// import net.minecraft.world.World;
// import net.minecraftforge.fml.relauncher.Side;
// import net.minecraftforge.fml.relauncher.SideOnly;
//
// @SideOnly(Side.CLIENT)
// public abstract class AvatarParticle extends EntityFX {
//
// private static final ResourceLocation VANILLA_PARTICLES = new
// ResourceLocation("textures/particle/particles.png");
//
// float smokeParticleScale;
//
// private boolean additiveBlending;
//
// public AvatarParticle(World world, double x, double y, double z, double motionX, double motionY,
// double motionZ) {
// super(world, x, y, z, motionX, motionY, motionZ);
// this.particleRed = 1.0F;
// this.particleGreen = 1.0F;
// this.particleBlue = 1.0F;
// this.setParticleTextureIndex(0);
// this.setSize(0.02F, 0.02F);
// this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
// this.motionX = motionX * 0.20000000298023224D + (double) ((float) (Math.random() * 2.0D - 1.0D) *
// 0.02F);
// this.motionY = motionY * 0.20000000298023224D + (double) ((float) (Math.random() * 2.0D - 1.0D) *
// 0.02F);
// this.motionZ = motionZ * 0.20000000298023224D + (double) ((float) (Math.random() * 2.0D - 1.0D) *
// 0.02F);
// this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
// this.particleIcon = newIcon();
// this.additiveBlending = false;
// }
//
// public void renderParticle(Tessellator t, float p_70539_2_, float p_70539_3_, float p_70539_4_,
// float p_70539_5_, float p_70539_6_,
// float p_70539_7_) {
// t.draw();
// if (additiveBlending) GlStateManager.glBlendFunc(GlStateManager.GL_SRC_ALPHA, GlStateManager.GL_ONE);
// Minecraft.getMinecraft().renderEngine.bindTexture(getTexture());
// t.setBrightness(200);
// t.startDrawingQuads();
// getIcon().setAnimation((int) ((1.0 * this.particleAge / this.particleMaxAge) * 7));
// this.particleScale = 4;
// super.renderParticle(t, p_70539_2_, p_70539_3_, p_70539_4_, p_70539_5_, p_70539_6_, p_70539_7_);
// t.draw();
// Minecraft.getMinecraft().renderEngine.bindTexture(VANILLA_PARTICLES);
// if (additiveBlending) GlStateManager.glBlendFunc(GlStateManager.GL_SRC_ALPHA, GlStateManager.GL_ONE_MINUS_SRC_ALPHA);
// t.startDrawingQuads();
// }
//
// /**
// * Called to update the entity's position/logic.
// */
// public void onUpdate() {
// super.onUpdate();
// this.motionY += 0.002;
// }
//
// private IconParticle getIcon() {
// return (IconParticle) particleIcon;
// }
//
// public abstract ResourceLocation getTexture();
//
// protected abstract IconParticle newIcon();
//
// protected void enableAdditiveBlending() {
// this.additiveBlending = true;
// }
//
// }