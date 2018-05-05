package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.common.entity.EntityAvatarLightning;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class AvatarEventHandler implements Mod.EventHandler {
	/*@SubscribeEvent
	public void LightningEvent(EntityStruckByLightningEvent event) {
		if (event.getLightning() instanceof EntityAvatarLightning){
			event.setCanceled(true);
		}
	}**/
}
