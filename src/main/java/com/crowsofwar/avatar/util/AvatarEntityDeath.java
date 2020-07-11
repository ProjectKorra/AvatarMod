package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.util.analytics.AnalyticEvents;
import com.crowsofwar.avatar.util.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarEntityDeath {

	@SubscribeEvent
	public static void onMobDeath(LivingDeathEvent e) {

		EntityLivingBase killed = e.getEntityLiving();
		DamageSource source = e.getSource();

		boolean av2EntityKilled = killed instanceof EntityBender;
		boolean abilityUsed = AvatarDamageSource.isAvatarDamageSource(source);

		if (av2EntityKilled || abilityUsed) {
			if (source.getTrueSource() instanceof EntityPlayer) {

				String mobName = EntityList.getEntityString(killed);
				AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onMobKill(mobName, source));

			}
		}

	}

}
