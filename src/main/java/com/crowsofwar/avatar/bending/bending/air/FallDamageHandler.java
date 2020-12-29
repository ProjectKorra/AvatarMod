package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;


@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FallDamageHandler {
	//TODO: ADD SLOW FALL WHEN SNEAKING FOR 1.13 INSTEAD OF CANCELLING THE DAMAGE
	@SubscribeEvent
	public static void noFallDamage(LivingHurtEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getEntity();
		if (entity instanceof EntityBender || entity instanceof EntityPlayer) {
			Bender bender = Bender.get(entity);
			if (bender != null) {
				BendingData ctx = BendingData.getFromEntity(entity);
				if (ctx != null && ctx.hasBendingId(Airbending.ID)) {
					if (STATS_CONFIG.passiveSettings.noFallDamageAir) {
						if (event.getSource() == DamageSource.FALL) {
							event.setAmount(0);
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
}
