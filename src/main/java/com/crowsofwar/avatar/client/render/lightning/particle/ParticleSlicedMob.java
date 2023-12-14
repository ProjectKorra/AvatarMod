package com.crowsofwar.avatar.client.render.lightning.particle;

import com.crowsofwar.avatar.client.render.lightning.configs.LightningConfig;
import com.crowsofwar.avatar.client.render.lightning.handler.HbmShaderManager2;
import com.crowsofwar.avatar.client.render.lightning.math.Vec3;
import com.crowsofwar.avatar.client.render.lightning.misc.RigidBody;
import com.crowsofwar.avatar.client.render.lightning.render.RenderHelper;
import com.crowsofwar.avatar.network.AvatarClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticleSlicedMob extends Particle {

	public ResourceLocation capTex;
	public ResourceLocation texture;
	public float capBloom;
	public int cutMob;
	public int cap;
	public RigidBody body;
	
	public ParticleSlicedMob(World worldIn, RigidBody body, int cutMob, int cap, ResourceLocation tex, ResourceLocation capTex, float capBloom) {
		super(worldIn, body.globalCentroid.xCoord, body.globalCentroid.yCoord, body.globalCentroid.zCoord);
		this.body = body;
		this.cutMob = cutMob;
		this.cap = cap;
		this.capTex = capTex;
		this.capBloom = capBloom;
		this.texture = tex;
		this.particleMaxAge = 80 + worldIn.rand.nextInt(20);
	}
	
	@Override
	public void onUpdate() {
		body.minecraftTimestep();
		this.posX = body.globalCentroid.xCoord;
		this.posY = body.globalCentroid.yCoord;
		this.posZ = body.globalCentroid.zCoord;
		this.particleAge ++;
		if(particleAge >= particleMaxAge){
			setExpired();
			GL11.glDeleteLists(cutMob, 1);
			GL11.glDeleteLists(cap, 1);
		}
	}
	
	@Override
	public int getFXLayer() {
		return 3;
	}
	
	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		GL11.glPushMatrix();
		GlStateManager.enableCull();
		GlStateManager.enableLighting();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableRescaleNormal();
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
		int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k, j);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		RenderHelper.resetParticleInterpPos(entityIn, partialTicks);
		RenderHelper.resetColor();
		body.doGlTransform(new Vec3(interpPosX, interpPosY, interpPosZ), partialTicks);
		GL11.glCallList(cutMob);
		Minecraft.getMinecraft().getTextureManager().bindTexture(capTex);
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(-1, -1);
		float lx = OpenGlHelper.lastBrightnessX;
		float ly = OpenGlHelper.lastBrightnessY;
		if(capBloom > 0){
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		}
		GL11.glCallList(cap);
		
		
		if(capBloom > 0 && LightningConfig.bloom){
			float[] matrix = new float[16];
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, AvatarClientProxy.AUX_GL_BUFFER);
			AvatarClientProxy.AUX_GL_BUFFER.get(matrix);
			AvatarClientProxy.AUX_GL_BUFFER.rewind();
			AvatarClientProxy.deferredRenderers.add(() -> {
				GL11.glPushMatrix();
				AvatarClientProxy.AUX_GL_BUFFER.put(matrix);
				AvatarClientProxy.AUX_GL_BUFFER.rewind();
				GL11.glLoadMatrix(AvatarClientProxy.AUX_GL_BUFFER);
				HbmShaderManager2.bloomData.bindFramebuffer(false);
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(capTex);
				GlStateManager.enablePolygonOffset();
				GlStateManager.disableLighting();
				float x = OpenGlHelper.lastBrightnessX;
				float y = OpenGlHelper.lastBrightnessY;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
				GlStateManager.doPolygonOffset(-1, -1);
				GL11.glCallList(cap);
				GlStateManager.disablePolygonOffset();
				GlStateManager.enableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, x, y);
				
				Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
				GL11.glPopMatrix();
			});
		}
		
		if(capBloom > 0){
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lx, ly);
		}
		GlStateManager.disablePolygonOffset();
		GlStateManager.disableRescaleNormal();
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GL11.glPopMatrix();
	}

}
