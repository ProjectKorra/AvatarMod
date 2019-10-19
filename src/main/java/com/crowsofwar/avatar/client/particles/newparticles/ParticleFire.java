package com.crowsofwar.avatar.client.particles.newparticles;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ParticleFire extends ParticleAvatar {

	// 1 different animation strip, 7 frames
	private static final ResourceLocation[][] TEXTURES = generateTextures("fire", 1, 7);

	public ParticleFire(World world, double x, double y, double z){

		super(world, x, y, z, TEXTURES[world.rand.nextInt(TEXTURES.length)]);

		this.setRBGColorF(1, 1, 1);
		this.particleAlpha = 1;
		this.particleMaxAge = 12 + rand.nextInt(4);
		this.shaded = false;
		this.canCollide = true;
	}

	@SubscribeEvent
	public static void onTextureStitchEvent(TextureStitchEvent.Pre event){
		for(ResourceLocation[] array : TEXTURES){
			for(ResourceLocation texture : array){
				event.getMap().registerSprite(texture);
			}
		}
	}
}
