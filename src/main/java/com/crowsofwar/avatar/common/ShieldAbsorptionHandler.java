package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;
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

		if (Bender.isBenderSupported(attacked)) {
			BendingData data = Bender.get(attacked).getData();
			if (data.hasStatusControl(StatusControl.BUBBLE_CONTRACT)) {
				EntityAirBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityAirBubble.class,
						attacked);
				if (bubble != null) {
					if (bubble.attackEntityFrom(e.getSource(), e.getAmount())) {
						e.setCanceled(true);
						world.playSound(null, attacked.getPosition(), SoundEvents.BLOCK_CLOTH_HIT,
								SoundCategory.PLAYERS, 1, 1);
					}
				}
			}
		}

	}

}
