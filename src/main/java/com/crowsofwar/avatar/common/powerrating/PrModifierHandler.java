package com.crowsofwar.avatar.common.powerrating;

import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.PowerRatingManager;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.Raytrace;

import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class PrModifierHandler {

	public static void addPowerRatingModifiers(Bender bender) {

		BendingContext ctx = new BendingContext(bender.getData(), bender.getEntity(),
				new Raytrace.Result());

		if (bender.getEntity().ticksExisted % 10 != 0) {
			return;
		}

		BendingData data = bender.getData();
		for (UUID bendingId : data.getAllBendingIds()) {

			PowerRatingManager manager = data.getPowerRatingManager(bendingId);

			if (bendingId.equals(Firebending.ID)) {
				if (!manager.hasModifier(FirebendingSunModifier.class)) {
					manager.addModifier(new FirebendingSunModifier(), ctx);
				}
			}
			if (bendingId.equals(Waterbending.ID)) {
				if (!manager.hasModifier(WaterbendingMoonBonus.class)) {
					manager.addModifier(new WaterbendingMoonBonus(), ctx);
				}
			}

		}

	}

}
