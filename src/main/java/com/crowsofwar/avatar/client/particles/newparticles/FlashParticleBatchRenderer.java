package com.crowsofwar.avatar.client.particles.newparticles;

import com.crowsofwar.avatar.AvatarInfo;
import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;

import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;

//Drillgon200: Like the vanilla particle manager, but supports more gl states.
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = AvatarInfo.MOD_ID)
public class FlashParticleBatchRenderer {

    private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("minecraft", "textures/particle/particles.png");
    private static final Queue<ParticleFlash> queue = Queues.<ParticleFlash>newArrayDeque();
    @SuppressWarnings("unchecked")
    public static ArrayDeque<ParticleFlash>[] particles = new ArrayDeque[2];

    static {
        for (int i = 0; i < particles.length; i++) {
            particles[i] = Queues.newArrayDeque();
        }
    }

    public static void addParticle(ParticleFlash p) {
        if (p != null)
            queue.add(p);
    }

    public static void updateParticles() {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().isGamePaused())
            return;
        for (ArrayDeque<ParticleFlash> a : particles) {
            Iterator<ParticleFlash> itr = a.iterator();
            while (itr.hasNext()) {
                ParticleFlash p = itr.next();
                p.onUpdate();
                if (!p.isAlive()) {
                    itr.remove();
                }
            }
        }

        if (!queue.isEmpty()) {
            for (ParticleFlash particle = queue.poll(); particle != null; particle = queue.poll()) {
                int glow = particle.glow ? 1 : 0;
                //Size time: 2^14 * 1.5
                if (particles[glow].size() > 16384)
                    particles[glow].removeFirst();
                particles[glow].add(particle);
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

        if (CLIENT_CONFIG.particleSettings.releaseShaderOnFlashParticleRender && GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) != 0) {
            GL20.glUseProgram(0);
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(PARTICLE_TEXTURES);

        for (int i = 0; i < particles.length; i++) {
            if (i == 0) {
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            } else if (i == 1) {
                GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
            }


            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            particles[i].forEach(particle -> particle.renderParticle(bufferbuilder, entityIn, partialTicks, f, f4, f1, f2, f3));

            tessellator.draw();
        }

        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    @SubscribeEvent
    public static void renderLast(RenderWorldLastEvent event) {
        renderParticles(Objects.requireNonNull(Minecraft.getMinecraft().getRenderViewEntity()), event.getPartialTicks());
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent event) {
        if (event.phase == Phase.START) {
            updateParticles();
        }
    }
}
