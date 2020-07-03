package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandlerController;
import com.crowsofwar.avatar.common.event.ParticleCollideEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.crowsofwar.avatar.common.bending.fire.tickhandlers.FlamethrowerUpdateTick.attackEntity;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FireEventListener {
	@SubscribeEvent
	public static void particleEventTest(ParticleCollideEvent event) {
		if (event.getSpawner() != event.getEntity()) {
			if (event.getAbility() instanceof AbilityFlamethrower) {
				if (event.getSpawner() instanceof EntityLivingBase) {
					EntityLivingBase entity = (EntityLivingBase) event.getSpawner();
					BendingData data = BendingData.getFromEntity(entity);
					if (data != null) {
						if (data.hasTickHandler(TickHandlerController.FLAMETHROWER)) {
							attackEntity(entity, event.getEntity());
						}
					}
				}
			}
		}
	}
}