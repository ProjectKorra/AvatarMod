// package com.crowsofwar.avatar.client.particles;
//
// import net.minecraft.util.ResourceLocation;
// import net.minecraft.world.World;
// import net.minecraftforge.fml.relauncher.Side;
// import net.minecraftforge.fml.relauncher.SideOnly;
//
// @SideOnly(Side.CLIENT)
// public class ParticleFlame extends AvatarParticle {
//
// public static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
// "textures/particles/flame.png");
//
// public ParticleFlame(World world, double x, double y, double z, double motionX, double motionY,
// double motionZ) {
// super(world, x, y, z, motionX, motionY, motionZ);
// enableAdditiveBlending();
// }
//
// @Override
// public ResourceLocation getTexture() {
// return TEXTURE;
// }
//
// @Override
// protected IconParticle newIcon() {
// return new IconParticle("Flame", 32, 32, 0, 0, 256, 256, 32);
// }
//
// }