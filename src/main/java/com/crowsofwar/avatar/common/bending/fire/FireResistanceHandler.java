package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FireResistanceHandler {

	@SubscribeEvent
	public static void onEntityHurtWithFire(LivingHurtEvent event) {
		Entity e = event.getEntity();
		Entity source = event.getSource().getImmediateSource();
		float amount = event.getAmount();
		if (e instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) e;
			if (source != null) {
				if (source instanceof AvatarEntity) {
					if (entity.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
						if (((AvatarEntity) source).getElement() instanceof Firebending && !entity.world.isRemote) {
							event.setAmount(amount - (Objects.requireNonNull(entity.getActivePotionEffect(MobEffects.FIRE_RESISTANCE)).getAmplifier() + 1) / 2F);
						}
					}
				}
			}
		}
	}
}
