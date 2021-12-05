package com.crowsofwar.avatar.client.particles.newparticles;

import com.crowsofwar.avatar.client.render.lightning.main.ResourceManager;
import com.crowsofwar.avatar.network.AvatarClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticleLightningHandGlow extends ParticleAvatar {

	public ParticleLightningHandGlow(World worldIn, double posXIn, double posYIn, double posZIn, float scale, int age) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.particleScale = scale;
		this.particleMaxAge = age;
	}
	
	public ParticleLightningHandGlow color(float r, float g, float b, float a){
		this.particleRed = r;
		this.particleGreen = g;
		this.particleBlue = b;
		this.particleAlpha = a;
		return this;
	}
	
	@Override
	public void onUpdate() {
		this.particleAge ++;
		if(particleAge >= particleMaxAge){
			setExpired();
			return;
		}
	}

	@Override
	public int getFXLayer() {
		return 3;
	}
	
	@Override
	public boolean shouldDisableDepth() {
		return true;
	}
	
	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		GL11.glPushMatrix();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.disableAlpha();
		
		float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks);
	    float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks);
	    float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks);
	    GL11.glTranslated(f5, f6, f7);
	    
	    
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, AvatarClientProxy.AUX_GL_BUFFER);
		AvatarClientProxy.AUX_GL_BUFFER.put(0, 1);
		AvatarClientProxy.AUX_GL_BUFFER.put(1, 0);
		AvatarClientProxy.AUX_GL_BUFFER.put(2, 0);
			
		AvatarClientProxy.AUX_GL_BUFFER.put(4, 0);
		AvatarClientProxy.AUX_GL_BUFFER.put(5, 1);
		AvatarClientProxy.AUX_GL_BUFFER.put(6, 0);
			
		AvatarClientProxy.AUX_GL_BUFFER.put(8, 0);
		AvatarClientProxy.AUX_GL_BUFFER.put(9, 0);
		AvatarClientProxy.AUX_GL_BUFFER.put(10, 1);
			
		GL11.glLoadMatrix(AvatarClientProxy.AUX_GL_BUFFER);

		
		float ageN = (float)(this.particleAge+partialTicks)/(float)this.particleMaxAge;
		float scale = MathHelper.clamp(ageN*2, 0, 1)* MathHelper.clamp(2-ageN*2+0.1F, 0, 1);
		float f4 = 0.1F * this.particleScale * scale;
        
		Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.fresnel_ms);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		buffer.pos(f4, f4, 0).tex(1, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
        buffer.pos(-f4, f4, 0).tex(1, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
        buffer.pos(-f4, -f4, 0).tex(0, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
        buffer.pos(f4, -f4, 0).tex(0, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
        Tessellator.getInstance().draw();
        
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GL11.glPopMatrix();
	}
	
}