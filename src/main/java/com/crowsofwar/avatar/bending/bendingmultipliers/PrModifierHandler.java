package com.crowsofwar.avatar.bending.bendingmultipliers;

import com.crowsofwar.avatar.item.ItemHangGliderBase;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.bending.bending.air.powermods.StaffPowerModifier;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.bending.bending.earth.EarthbendingJingModifier;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.PowerRatingManager;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.util.Raytrace;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
			if (bendingId.equals(Earthbending.ID)) {
				if (!manager.hasModifier(EarthbendingJingModifier.class)) {
					manager.addModifier(new EarthbendingJingModifier(), ctx);
				}
			}
			if (bendingId.equals(Airbending.ID)) {
				if (bender.getEntity().getHeldItemMainhand() != ItemStack.EMPTY) {
					Item item = bender.getEntity().getHeldItemMainhand().getItem();
					if (item instanceof ItemHangGliderBase) {
						manager.addModifier(new StaffPowerModifier(), ctx);
					}
				}
				if (bender.getEntity().getHeldItemOffhand() != ItemStack.EMPTY) {
					Item item = bender.getEntity().getHeldItemOffhand().getItem();
					if (item instanceof ItemHangGliderBase) {
						manager.addModifier(new StaffPowerModifier(), ctx);
					}
				}
			}

		}

	}

}
