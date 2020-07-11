package com.crowsofwar.avatar.client.gui;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.event.BendingCycleEvent;
import com.crowsofwar.avatar.util.event.BendingUseEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.util.data.TickHandlerController.RENDER_ELEMENT_HANDLER;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FadeElementListener {

	@SubscribeEvent
	public static void onBendingCycleEvent(BendingCycleEvent event) {
		if (event.getEntityLiving() != null) {
			EntityLivingBase entity = event.getEntityLiving();
			BendingData data = BendingData.getFromEntity(entity);
			if (data != null) {
				if (CLIENT_CONFIG.activeBendingSettings.shouldBendingMenuDisappear) {
					if (data.hasTickHandler(RENDER_ELEMENT_HANDLER)) {
						data.removeTickHandler(RENDER_ELEMENT_HANDLER);
						data.addTickHandler(RENDER_ELEMENT_HANDLER);
					} else {
						data.addTickHandler(RENDER_ELEMENT_HANDLER);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onBendingOpenEvent(BendingUseEvent event) {
		if (event.getEntityLiving() != null) {
			EntityLivingBase entity = event.getEntityLiving();
			BendingData data = BendingData.getFromEntity(entity);
			if (data != null) {
				if (CLIENT_CONFIG.activeBendingSettings.shouldBendingMenuDisappear) {
					if (data.hasTickHandler(RENDER_ELEMENT_HANDLER)) {
						data.removeTickHandler(RENDER_ELEMENT_HANDLER);
						data.addTickHandler(RENDER_ELEMENT_HANDLER);
					} else {
						data.addTickHandler(RENDER_ELEMENT_HANDLER);
					}
				}
			}
		}
	}
}
