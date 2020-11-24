package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.util.analytics.AnalyticEvents;
import com.crowsofwar.avatar.util.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarPlayerDeath {

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent e) {

		EntityLivingBase died = e.getEntityLiving();
		if (died instanceof EntityPlayer) {

			Bender bender = Bender.get(died);
			//noinspection ConstantConditions
			bender.onDeath();

			sendDeathAnalytic(e);

		}

	}

	/**
	 * Possibly sends analytics for the player being killed by PvP or Av2 entity.
	 */
	private static void sendDeathAnalytic(LivingDeathEvent e) {

		if (!e.getEntity().world.isRemote) {
			DamageSource source = e.getSource();
			Entity causeEntity = source.getTrueSource();

			if (causeEntity instanceof EntityPlayer) {
				if (AvatarDamageSource.isAvatarDamageSource(source)) {
					// Chop off initial "avatar_" from the damage source name
					String dsName = source.getDamageType().substring("avatar_".length());
					AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onPvpKillWithAbility(dsName));
				}
			}

			if (causeEntity instanceof EntityBender) {
				String mobName = EntityList.getEntityString(causeEntity);
				AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onPlayerDeathWithMob(mobName));
			}

		}

	}

}
