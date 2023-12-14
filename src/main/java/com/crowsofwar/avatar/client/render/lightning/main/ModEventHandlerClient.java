package com.crowsofwar.avatar.client.render.lightning.main;

import com.crowsofwar.avatar.client.render.lightning.particle.ParticleFirstPerson;
import com.google.common.collect.Queues;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.Display;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class ModEventHandlerClient {

	public static Set<EntityLivingBase> specialDeathEffectEntities = new HashSet<>();
	public static ArrayDeque<ParticleFirstPerson> firstPersonAuxParticles = Queues.newArrayDeque();

	public static float deltaMouseX;
	public static float deltaMouseY;
	
	public static float currentFOV = 70;
	
	public static void updateMouseDelta() {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.inGameHasFocus && Display.isActive()) {
			mc.mouseHelper.mouseXYChange();
			float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f1 = f * f * f * 8.0F;
			deltaMouseX = (float) mc.mouseHelper.deltaX * f1;
			deltaMouseY = (float) mc.mouseHelper.deltaY * f1;
		} else {
			deltaMouseX = 0;
			deltaMouseY = 0;
		}
	}

//	@SubscribeEvent
//	public void renderHand(RenderHandEvent e){
//		if(Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof IPostRender || Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() instanceof IPostRender){
//			e.setCanceled(true);
//			Minecraft mc = Minecraft.getMinecraft();
//			boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)mc.getRenderViewEntity()).isPlayerSleeping();
//			if (mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator())
//	        {
//	            mc.entityRenderer.enableLightmap();
//	            mc.entityRenderer.itemRenderer.renderItemInFirstPerson(e.getPartialTicks());
//	            mc.entityRenderer.disableLightmap();
//	        }
//			HbmShaderManager2.postProcess();
//		}
//	}
	
//	@SubscribeEvent(priority = EventPriority.HIGHEST)
//	public void cancelVanished(RenderLivingEvent.Pre<EntityLivingBase> event){
//		if(AvatarMod.proxy.isVanished(event.getEntity())){
//			event.setCanceled(true);
//		}
//	}

}
