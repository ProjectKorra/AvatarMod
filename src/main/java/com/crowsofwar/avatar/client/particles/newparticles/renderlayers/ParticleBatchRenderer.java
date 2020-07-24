package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.client.particles.newparticles.ParticleAvatar;
import com.google.common.collect.Queues;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//Drillgon200: Like the vanilla particle manager, but supports more gl states.
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = AvatarInfo.MOD_ID)
public class ParticleBatchRenderer {
	
	public static Set<RenderLayer> layers = new HashSet<>();

	private static final Queue<ParticleAvatar> queue = Queues.<ParticleAvatar> newArrayDeque();

	public static void registerRenderLayer(RenderLayer r){
		layers.add(r);
	}
	
	public static void addParticle(ParticleAvatar p) {
		if(p != null)
			queue.add(p);
	}

	public static void updateParticles() {
		if(Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().isGamePaused())
			return;
		for(RenderLayer layer : layers){
			Iterator<ParticleAvatar> itr = layer.particles.iterator();
			while(itr.hasNext()) {
				ParticleAvatar p = itr.next();
				p.onUpdate();
				if(!p.isAlive()) {
					itr.remove();
				}
			}
		}

		if(!queue.isEmpty()) {
			for(ParticleAvatar particle = queue.poll(); particle != null; particle = queue.poll()) {
				RenderLayer layer = particle.getCustomRenderLayer();
				
				if(layer == null){
					throw new RuntimeException("Particle " + particle + " does not use a custom render layer!");
				} else if(!layers.contains(layer)){
					layers.add(layer);
				}
				
				if(layer.particles.size() > 16384)
					layer.particles.removeFirst();
				
				layer.particles.add(particle);
			}
		}
	}

	public static void renderParticles(Entity entityIn, float partialTicks) {
		float f = ActiveRenderInfo.getRotationX();
		float f1 = ActiveRenderInfo.getRotationZ();
		float f2 = ActiveRenderInfo.getRotationYZ();
		float f3 = ActiveRenderInfo.getRotationXY();
		float f4 = ActiveRenderInfo.getRotationXZ();
		Particle.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
		Particle.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
		Particle.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
		Particle.cameraViewDir = entityIn.getLook(partialTicks);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
		GlStateManager.depthMask(false);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		for(RenderLayer layer : layers) {
			layer.preRenderParticles();
			layer.particles.forEach(particle -> particle.renderParticle(Tessellator.getInstance().getBuffer(), entityIn, partialTicks, f, f4, f1, f2, f3));
			layer.postRenderParticles();
		}

		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
	}

	@SubscribeEvent
	public static void renderLast(RenderWorldLastEvent event) {
		renderParticles(Minecraft.getMinecraft().getRenderViewEntity(), event.getPartialTicks());
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent event) {
		if(event.phase == Phase.START) {
			updateParticles();
		}
	}

}
