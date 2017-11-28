package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.data.Bender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarPlayerDeath {

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent e) {

		EntityLivingBase died = e.getEntityLiving();
		if (died instanceof EntityPlayer) {

			Bender bender = Bender.get(died);
			//noinspection ConstantConditions
			bender.onDeath();

		}

	}

}
