package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import java.util.ArrayDeque;
import java.util.Collection;

import org.lwjgl.opengl.GL11;

import com.crowsofwar.avatar.client.particles.newparticles.ParticleAvatar;
import com.google.common.collect.Queues;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public abstract class RenderLayer {

	protected ArrayDeque<ParticleAvatar> particles = Queues.newArrayDeque();
	
	//All render layers should be singletons
	protected RenderLayer() {
	}
	
	public void preRenderParticles(){
		Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}
	
	public void postRenderParticles(){
		Tessellator.getInstance().draw();
	}
	
	public Collection<ParticleAvatar> getParticles(){
		return particles;
	}
}
