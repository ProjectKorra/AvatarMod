package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.Sys;


@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FallDamageHandler {
	//TODO: ADD SLOW FALL WHEN SNEAKING FOR 1.13 INSTEAD OF CANCELLING 1.13
	@SubscribeEvent
	public static void noFallDamage(LivingHurtEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getEntity();
		if (entity instanceof EntityBender || entity instanceof EntityPlayerMP) {
			Bender bender = Bender.get(entity);
			if (bender.getData() != null) {
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
