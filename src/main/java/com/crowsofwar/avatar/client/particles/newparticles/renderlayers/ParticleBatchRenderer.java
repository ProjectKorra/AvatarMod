package com.crowsofwar.avatar.client.particles.newparticles.renderlayers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
	public static Map<BlockPos, ParticleGroup> collidingParticlesByPositions = new HashMap<>();

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
		
		collidingParticlesByPositions.clear();
		for(RenderLayer layer : layers){
			for(ParticleAvatar p : layer.particles){
				if(p.canCollideParticles){
					BlockPos pos = p.getPosition();
					if(collidingParticlesByPositions.containsKey(pos)){
						collidingParticlesByPositions.get(pos).addParticle(p);
					} else {
						ParticleGroup group = new ParticleGroup();
						group.addParticle(p);
						collidingParticlesByPositions.put(pos, group);
					}
				}
			}
		}
		
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
	
	//List of lists so you don't have to constantly be copying items from one list to another.
	//Could be much cleaner with a custom list implementation, but I'm too lazy to do that.
	public static List<List<ParticleAvatar>> getCollidingParticlesWithinBBExcluding(Entity e, AxisAlignedBB bb){
		bb = bb.grow(ParticleAvatar.MAX_PARTICLE_SIZE);
		int x1 = MathHelper.floor(bb.minX);
		int x2 = MathHelper.ceil(bb.maxX);
		int y1 = MathHelper.floor(bb.minY);
		int y2 = MathHelper.ceil(bb.maxY);
		int z1 = MathHelper.floor(bb.minZ);
		int z2 = MathHelper.ceil(bb.maxZ);
		
		List<List<ParticleAvatar>> list = new ArrayList<>();
		
		for(int x = x1; x <= x2; x ++){
			for(int y = y1; y <= y2; y ++){
				for (int z = z1; z <= z2; z ++){
					ParticleGroup group = collidingParticlesByPositions.get(new BlockPos(x, y, z));
					if(group != null)
						group.getParticlesExcludingEntity(e, list);
				}
			}
		}
		
		return list;
	}
	
	private static class ParticleGroup {
		List<Pair<Entity, List<ParticleAvatar>>> containedParticles = new ArrayList<>();
		
		public void addParticle(ParticleAvatar p){
			for(Pair<Entity, List<ParticleAvatar>> pair : containedParticles){
				if(pair.getKey() == p.getEntity()){
					pair.getValue().add(p);
					return;
				}
			}
			List<ParticleAvatar> list = new ArrayList<>();
			list.add(p);
			containedParticles.add(Pair.of(p.getEntity(), list));
		}
		
		public void getParticlesExcludingEntity(Entity e, List<List<ParticleAvatar>> list){
			for(Pair<Entity, List<ParticleAvatar>> pair : containedParticles){
				if(e == null || pair.getKey() != e){
					list.add(pair.getValue());
				}
			}
		}
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
