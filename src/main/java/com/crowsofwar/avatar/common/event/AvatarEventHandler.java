package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityAvatarLightning;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
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
