package com.crowsofwar.avatar.common.event;

import net.minecraft.util.DamageSource;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.entity.AvatarEntity;

@Mod.EventBusSubscriber(modid = AvatarInfo.MODID)
public abstract class AvatarEventHandler {
	@SubscribeEvent
	public void onLivingHurtEvent(LivingHurtEvent event) {
		if (event.getSource() == DamageSource.LIGHTNING_BOLT || event.getSource() == AvatarDamageSource.LIGHTNING) {
			if (event.getEntity() instanceof AvatarEntity) {
				((AvatarEntity) event.getEntity()).onLightningContact();
			}
		}
	}
}
