package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.FIRE_DEVOUR_HANDLER;


@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FireDevourHandler {

	//@SubscribeEvent
	public static void onShift(LivingEvent.LivingUpdateEvent event) {
		Entity e = event.getEntity();
		World world = e.getEntityWorld();
		if (e == event.getEntity()) {
			if (e instanceof EntityPlayer || e instanceof EntityBender) {
				BendingData data = BendingData.get((EntityLivingBase) e);
				Bender b = Bender.get((EntityLivingBase) e);
				if (b != null) {
					if (e.isSneaking() && STATS_CONFIG.shiftActivateFireDevour) {
						if (data.hasBendingId(Firebending.ID)) {
							data.addTickHandler(FIRE_DEVOUR_HANDLER);
						}

					}
				}
			}
		}
	}
}
