package com.crowsofwar.avatar.common.bending.air;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;

@Mod.EventBusSubscriber(modid = AvatarInfo.MODID)
public class FallDamageHandler {
	//TODO: ADD SLOW FALL WHEN SNEAKING FOR 1.13 INSTEAD OF CANCELLING THE DAMAGE
	@SubscribeEvent
	public static void noFallDamage(LivingHurtEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getEntity();
		if (entity instanceof EntityBender || entity instanceof EntityPlayer) {
			Bender bender = Bender.get(entity);
			if (bender != null) {
				BendingData ctx = BendingData.get(entity);
				if (ctx.hasBendingId(Airbending.ID)) {
					if (event.getSource() == DamageSource.FALL) {
						event.setAmount(0);
						event.setCanceled(true);
					}
				}
			}
		}
	}
}
