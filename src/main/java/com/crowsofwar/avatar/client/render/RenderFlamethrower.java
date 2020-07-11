package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntityFlame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RenderFlamethrower extends Render<EntityFlame> {

    //Although this is bad practice, mobs won't show particles otherwise.

    public RenderFlamethrower(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityFlame entity, double x, double y, double z, float entityYaw, float partialTicks) {

        float r, g, b, a;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        World world = entity.world;
        //GlStateManager.pushMatrix();
	/*	if (world.isRemote) {
			for (double i = 0; i < Math.max(Math.min((int) (1 / entity.getAvgSize()), 2), 1); i++) {
				AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
				double spawnX = boundingBox.getCenter().x + world.rand.nextGaussian() / 15;
				double spawnY = boundingBox.getCenter().y + world.rand.nextGaussian() / 15;
				double spawnZ = boundingBox.getCenter().z + world.rand.nextGaussian() / 15;
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(255, 10, 5)
						.scale(entity.getAvgSize() * 2F).element(entity.getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 30), 10)
						.scale(entity.getAvgSize() * 2F).element(entity.getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).clr(255, 10, 5)
						.scale(entity.getAvgSize() * 2F).element(entity.getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80, world.rand.nextGaussian() / 80,
						world.rand.nextGaussian() / 80).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 30), 10)
						.scale(entity.getAvgSize() * 2F).element(entity.getElement()).collide(true).spawn(world);
			}
		}**/
        //Copied from particleFlash.
		/*int maxFlashes = AvatarUtils.getRandomNumberInRange(2, 4);
		for (int i = 0; i < maxFlashes; i++) {
			if (i < (maxFlashes / 2)) {
				r = 1F;
				g = 10 / 255F;
				b = 5 / 255F;
			} else {
				r = (235 + AvatarUtils.getRandomNumberInRange(0, 20)) / 255F;
				g = (20 + AvatarUtils.getRandomNumberInRange(0, 30)) / 255F;
				b = 10 / 255F;
			}

			double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
			double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
			double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;

			float f4;
			if (CLIENT_CONFIG.particleSettings.voxelFlashParticles || CLIENT_CONFIG.particleSettings.squareFlashParticles) {
				f4 = entity.getAvgSize() * 2 * 0.725F * MathHelper.sin(((float) entity.ticksExisted + partialTicks - 1.0F) / (float) entity.getLifeTime() * (float) Math.PI);
			} else {
				f4 = entity.getAvgSize() * 2 * MathHelper.sin(((float) entity.ticksExisted + partialTicks - 1.0F) / (float) entity.getLifeTime() * (float) Math.PI);
			}
			//Great for fire!
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			GlStateManager.disableLighting();
			a = 0.6F - ((float) entity.ticksExisted + partialTicks - 1.0F) / (float) entity.getLifeTime() * 0.5F;
			float f5 = (float) entity.posX;//(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - interpPosX);
			float f6 = (float) entity.posY; //(entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - interpPosY);
			float f7 = (float) entity.posZ;  //(entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - interpPosZ);
			int h = entity.getBrightnessForRender();
			int j = h >> 16 & 65535;
			int k = h & 65535;
			if (CLIENT_CONFIG.particleSettings.voxelFlashParticles) {
				//	GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.SRC_ALPHA);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_CONSTANT_ALPHA, GlStateManager.DestFactor.ONE);
			}
			//TODO: Figure out a way to do this without breaking everything else
			if (CLIENT_CONFIG.particleSettings.squareFlashParticles) {
				GlStateManager.disableTexture2D();
				GlStateManager.disableNormalize();
			}

			float rotationX = ActiveRenderInfo.getRotationX();
			float rotationZ = ActiveRenderInfo.getRotationZ();
			float rotationYZ = ActiveRenderInfo.getRotationYZ();
			float rotationXY = ActiveRenderInfo.getRotationXY();
			float rotationXZ = ActiveRenderInfo.getRotationXZ();
			//cameraViewDir = entity.getLook(partialTicks);

			//X, Z, YZ, XY, XZ correspond to:
			//look z, look y, look x, look xy, look yz
			buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			buffer.pos(f5 - rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 - rotationYZ * f4 - rotationXZ * f4).tex(0.5D, 0.375D)
					.color(r, g, b, a).lightmap(j, k).endVertex();
			buffer.pos(f5 - rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 - rotationYZ * f4 + rotationXZ * f4).tex(0.5D, 0.125D)
					.color(r, g, b, a).lightmap(j, k).endVertex();
			buffer.pos(f5 + rotationX * f4 + rotationXY * f4, f6 + rotationZ * f4, f7 + rotationYZ * f4 + rotationXZ * f4).tex(0.25D, 0.125D)
					.color(r, g, b, a).lightmap(j, k).endVertex();
			buffer.pos(f5 + rotationX * f4 - rotationXY * f4, f6 - rotationZ * f4, f7 + rotationYZ * f4 - rotationXZ * f4).tex(0.25D, 0.375D)
					.color(r, g, b, a).lightmap(j, k).endVertex();
			tessellator.draw();
			//GlStateManager.popMatrix();
			GlStateManager.enableTexture2D();
			GlStateManager.enableNormalize();
		}**/
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityFlame entity) {
        return null;
    }
}
