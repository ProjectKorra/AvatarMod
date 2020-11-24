package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityShield;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class ShieldAbsorptionHandler {

	@SubscribeEvent
	public static void shieldEntityAbsorb(LivingAttackEvent e) {

		World world = e.getEntity().world;
		EntityLivingBase attacked = (EntityLivingBase) e.getEntity();

		EntityShield shield = AvatarEntity.lookupControlledEntity(world, EntityShield.class,
				attacked);

		if (shield != null) {
			if (shield.attackEntityFrom(e.getSource(), e.getAmount())) {
				e.setCanceled(true);
				world.playSound(null, attacked.getPosition(), SoundEvents.BLOCK_CLOTH_HIT,
						SoundCategory.PLAYERS, 1, 1);
			}
		}

	}

}
