package com.crowsofwar.avatar.client.particles.newparticles;

import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleWater extends ParticleAvatar {

	/**
	 * Creates a new particle in the given world at the given position. All other parameters are set via the various
	 * setter methods ({@link ParticleBuilder ParticleBuilder} deals with all of that anyway).
	 *
	 * @param world    The world in which to create the particle.
	 * @param x        The x-coordinate at which to create the particle.
	 * @param y        The y-coordinate at which to create the particle.
	 * @param z        The z-coordinate at which to create the particle.
	 * @param textures One or more {@code ResourceLocation}s representing the texture(s) used by this particle. These
	 *                 <b>must</b> be registered as {@link TextureAtlasSprite}s using {@link TextureStitchEvent} or the textures will be
	 *                 missing. If more than one {@code ResourceLocation} is specified, the particle will be animated with each texture
	 *                 shown in order for an equal proportion of the particle's lifetime. If this argument is omitted (or a zero-length
	 */
	public ParticleWater(World world, double x, double y, double z, ResourceLocation... textures) {
		super(world, x, y, z, textures);
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity viewer, float partialTicks, float lookZ, float lookY, float lookX, float lookXY, float lookYZ) {
		float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks);
		float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks);
		float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks);



	}
}
