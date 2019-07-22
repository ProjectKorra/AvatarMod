package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.AvatarInfo;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class WaterArcComboHandler {
	private static HashMap<String, Boolean> activatedWaterArcCombo = new HashMap<>();
	private static HashMap<String, Integer> waterArcComboTicks = new HashMap<>();

	static boolean getWaterArcActivatedCombo(String UUID) {
		return activatedWaterArcCombo.getOrDefault(UUID, false);
	}

	static void setWaterArcActivatedCombo(String UUID, boolean activated) {
		if (activatedWaterArcCombo.containsKey(UUID)) {
			activatedWaterArcCombo.replace(UUID, getWaterArcActivatedCombo(UUID), activated);
		} else {
			activatedWaterArcCombo.put(UUID, activated);
		}
	}

	private static int getWaterArcComboTicks(String UUID) {
		return waterArcComboTicks.getOrDefault(UUID, 0);
	}

	static void setWaterArcComboTicks(String UUID, int ticks) {
		if (waterArcComboTicks.containsKey(UUID)) {
			waterArcComboTicks.replace(UUID, getWaterArcComboTicks(UUID), ticks);
		} else {
			waterArcComboTicks.put(UUID, ticks);
		}
	}


	@SubscribeEvent
	public static void onUpdate(LivingEvent.LivingUpdateEvent event) {
		String UUID = event.getEntity().getUniqueID().toString();
		if (getWaterArcActivatedCombo(UUID)) {
			setWaterArcComboTicks(UUID, getWaterArcComboTicks(UUID) + 1);
		}
		if (getWaterArcComboTicks(UUID) >= 15) {
			setWaterArcActivatedCombo(UUID, false);
			setWaterArcComboTicks(UUID, 0);
		}
	}

}
