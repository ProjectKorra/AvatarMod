package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.client.particles.newparticles.ParticleLightningHandGlow;
import com.crowsofwar.avatar.client.render.lightning.animloader.AnimationWrapper;
import com.crowsofwar.avatar.client.render.lightning.handler.HbmShaderManager2;
import com.crowsofwar.avatar.client.render.lightning.main.ResourceManager;
import com.crowsofwar.avatar.client.particles.newparticles.ParticleLightningStrip;
import com.crowsofwar.avatar.AvatarInfo;

import com.crowsofwar.avatar.client.render.lightning.math.BobMathUtil;
import com.crowsofwar.avatar.client.render.lightning.render.Tessellator;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This is a port of Drillgon200's implementation of HBM's nuclear mod
 * with lightning particle animations. All relevant references to code
 * and imports were placed in their respective packages based on their
 * purpose.
 *   (i.e.: ParticleLightningStrip which extended Particle is located at
 *   com.crowsofwar.avatar.client.particles.newparticles
 *   and now extends ParticleAvatar)
 * Any other relevant code was placed in its own package wholesale.
 * Move, modify and rename it however you see fit!

 * Source Code Base: <a href=https://github.com/Drillgon200/Hbm-s-Nuclear-Tech-GIT/blob/1.12.2_test/src/main/java/com/hbm/handler/WorldSpaceFPRender.java"></a>

 * @author Drillgon200, modified by jakeee51
 * @see com.crowsofwar.avatar.client.render.lightning Relevant Resources
 * @since AvatarMod 1.6.0
 */

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = AvatarInfo.MOD_ID)
public class RenderLightningStripHbm {

    public static boolean shouldCustomRender = false;
    public static int ticksActive = -1;
    private static long renderTime;
    public static AnimationWrapper wrapper;
    public static List<ParticleLightningStrip> lightning_strips = new ArrayList<>();
    public static List<Particle> particles = new ArrayList<>();

//    @SubscribeEvent
//    public static void renderHand(RenderHandEvent e) {
//        if(true || !shouldCustomRender)
//            return;
//        e.setCanceled(true);
//    }

    @SubscribeEvent
    public static void doDepthRender(CameraSetup e){
        if(true || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0 || !shouldCustomRender)
            return;

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Project.gluPerspective(70, (float) Minecraft.getMinecraft().displayWidth / (float) Minecraft.getMinecraft().displayHeight, 0.05F, Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16F * 2.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glPushMatrix();

        GL11.glTranslated(-0.3, 0, -2.25);
        GL11.glRotated(90, 0, 1, 0);

        ResourceManager.lightning_fp.controller.setAnim(wrapper);
        GlStateManager.colorMask(false, false, false, false);
        ResourceManager.maxdepth.use();
        ResourceManager.lightning_fp.renderAnimated(renderTime = System.currentTimeMillis());
        Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItemInFirstPerson((float) e.getRenderPartialTicks());
        HbmShaderManager2.releaseShader();
        GlStateManager.colorMask(true, true, true, true);
        GL11.glPopMatrix();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }

    public static void doHandRendering(RenderWorldLastEvent e) {
        if(true || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0 || !shouldCustomRender)
            return;

        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Project.gluPerspective(70, (float) Minecraft.getMinecraft().displayWidth / (float) Minecraft.getMinecraft().displayHeight, 0.05F, Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16F * 2.0F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glPushMatrix();

        Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItemInFirstPerson(e.getPartialTicks());

		if(ticksActive >= 0){
			GL11.glPushMatrix();
			GL11.glTranslated(-0.3, 0, -2.25);
			GL11.glRotated(90, 0, 1, 0);

			RenderHelper.enableStandardItemLighting();
	        ResourceManager.lightning_fp.controller.setAnim(wrapper);
	        ResourceManager.lightning_fp.renderAnimated(renderTime, (last, first, model, diffN, name) -> {
	        	if(name.equals("lower")){
	        		if(ticksActive < 55)
	        		for(ParticleLightningStrip p : lightning_strips){
	        			p.setNewPoint(BobMathUtil.viewFromLocal(new Vector4f(0.156664F, -0.60966F, -0.252432F, 1))[0]);
	        		}
	        		for(Particle p : particles){
	        			p.renderParticle(Tessellator.getInstance().getBuffer(), Minecraft.getMinecraft().getRenderViewEntity(), e.getPartialTicks(), 0, 0, 0, 0, 0);
	        		}
                }
	        	return false;
	        });
	        GL11.glPopMatrix();
		}
        for(ParticleLightningStrip p : lightning_strips){
        	if(p != null)
        		p.renderParticle(Tessellator.getInstance().getBuffer(), Minecraft.getMinecraft().getRenderViewEntity(), e.getPartialTicks(), 0, 0, 0, 0, 0);
        }

        GL11.glPopMatrix();
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.ClientTickEvent e){
        if(true || e.phase == Phase.END || Minecraft.getMinecraft().world == null)
            return;
		Random rand = Minecraft.getMinecraft().world.rand;
		if(ticksActive >= 0){
			ticksActive ++;
			if(ticksActive >= 84){
				ticksActive = -1;
			}
			particles.add(new ParticleLightningHandGlow(Minecraft.getMinecraft().world, 0.156664F, -0.60966F, -0.252432F, 2+rand.nextFloat()*0.5F, 3+rand.nextInt(3)).color(0.8F, 0.9F, 1F, 1F));
		} else if(Keyboard.isKeyDown(Keyboard.KEY_I)) {
			ticksActive = 0;
			wrapper = new AnimationWrapper(System.currentTimeMillis(), ResourceManager.lightning_fp_anim).onEnd(new AnimationWrapper.EndResult(AnimationWrapper.EndType.END, null));
			lightning_strips.clear();
			lightning_strips.add(new ParticleLightningStrip(Minecraft.getMinecraft().world, 0, 0, 0));
			lightning_strips.add(new ParticleLightningStrip(Minecraft.getMinecraft().world, 0, 0, 0));
		}
		Iterator<ParticleLightningStrip> iter = lightning_strips.iterator();
		while(iter.hasNext()){
			Particle p = iter.next();
			p.onUpdate();
			if(!p.isAlive())
				iter.remove();
		}
		Iterator<Particle> iter2 = particles.iterator();
		while(iter2.hasNext()){
			Particle p = iter2.next();
			p.onUpdate();
			if(!p.isAlive())
				iter2.remove();
		}
    }
}
