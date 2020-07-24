package com.crowsofwar.avatar.client.particles.newparticles;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.client.particles.newparticles.renderlayers.RenderLayer;
import com.crowsofwar.avatar.client.particles.newparticles.renderlayers.RenderLayerAdditive;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = AvatarInfo.MOD_ID)
public class ParticleFire extends ParticleAvatar {


	// 1 different animation strip, 7 frames
	private static final ResourceLocation[] TEXTURES = generateTextures("fire", 7);

	public ParticleFire(World world, double x, double y, double z) {

		super(world, x, y, z, TEXTURES);

		this.setRBGColorF(1, 1, 1);
		this.particleAlpha = 0.875F;
		this.particleMaxAge = 12 + rand.nextInt(4);
		this.shaded = false;
		this.canCollide = true;
	}

	@SubscribeEvent
	public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		for (ResourceLocation array : TEXTURES) {
			event.getMap().registerSprite(array);
		}
	}
	
	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.particleScale *= 1.055;
		this.motionX *= 0.99;
		this.motionY *= 0.99;
		this.motionZ *= 0.99;
	}

	@Override
	protected void drawParticle(BufferBuilder buffer, Entity viewer, float partialTicks, float rotationX, float rotationY, float rotationZ, float rotationYZ, float rotationXY) {
		super.drawParticle(buffer, viewer, partialTicks, rotationX, rotationY, rotationZ, rotationYZ, rotationXY);
	}
	
	@Override
	public RenderLayer getCustomRenderLayer() {
		return RenderLayerAdditive.INSTANCE;
	}
}
