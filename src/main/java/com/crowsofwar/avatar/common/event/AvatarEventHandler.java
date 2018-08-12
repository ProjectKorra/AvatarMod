package com.crowsofwar.avatar.common.event;

import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public abstract class AvatarEventHandler {
	/*@SubscribeEvent
	public static void LightningEvent(EntityStruckByLightningEvent event) {
		if (event.getLightning() instanceof EntityAvatarLightning){
			event.setCanceled(true);
		}
	}**/
}
