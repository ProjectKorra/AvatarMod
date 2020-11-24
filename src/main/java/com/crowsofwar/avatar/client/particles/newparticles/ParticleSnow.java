package com.crowsofwar.avatar.client.particles.newparticles;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.client.particles.newparticles.renderlayers.RenderLayer;
import com.crowsofwar.avatar.client.particles.newparticles.renderlayers.RenderLayerBlockSheet;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

//@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = AvatarInfo.MOD_ID)
public class ParticleSnow extends ParticleAvatar {

	private static final ResourceLocation[] TEXTURES = generateTextures("snow", 4);

	public ParticleSnow(World world, double x, double y, double z){
		
		super(world, x, y, z, TEXTURES[world.rand.nextInt(TEXTURES.length)]);
		this.setVelocity(0, -0.02, 0);
		this.particleScale *= 0.6f;
		this.particleGravity = 0;
		this.canCollide = true;
		this.setMaxAge(40 + rand.nextInt(10));
		// Produces a variety of light blues and whites
		this.setRBGColorF(0.9f + 0.1f * random.nextFloat(), 0.95f + 0.05f * random.nextFloat(), 1);
	}
	
	@SubscribeEvent
	public static void onTextureStitchEvent(TextureStitchEvent.Pre event){
		for(ResourceLocation texture : TEXTURES){
			event.getMap().registerSprite(texture);
		}
	}
	
	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		super.onUpdate();
	}
	
	@Override
	public void renderParticle(BufferBuilder buffer, Entity viewer, float partialTicks, float lookZ, float lookY,
			float lookX, float lookXY, float lookYZ) {
		// TODO Auto-generated method stub
		super.renderParticle(buffer, viewer, partialTicks, lookZ, lookY, lookX, lookXY, lookYZ);
	}
	
	@Override
	public RenderLayer getCustomRenderLayer() {
		return RenderLayerBlockSheet.INSTANCE;
	}
}
