package com.crowsofwar.avatar.common.powerrating;

import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingManager;

import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class PrModifierHandler {

	public static void addPowerRatingModifiers(Bender bender) {

		if (bender.getEntity().ticksExisted % 10 != 0) {
			return;
		}

		BendingData data = bender.getData();
		for (UUID bendingId : data.getAllBendingIds()) {

			PowerRatingManager manager = data.getPowerRatingManager(bendingId);

			if (bendingId.equals(Firebending.ID)) {
				if (!manager.hasModifier(FirebendingSunModifier.class)) {
					manager.addModifier(new FirebendingSunModifier());
				}
			}
			if (bendingId.equals(Waterbending.ID)) {
				if (!manager.hasModifier(WaterbendingMoonBonus.class)) {
					manager.addModifier(new WaterbendingMoonBonus());
				}
			}

		}

	}

}
