package com.crowsofwar.avatar.common;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.analytics.*;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;

@Mod.EventBusSubscriber(modid = AvatarInfo.MODID)
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
