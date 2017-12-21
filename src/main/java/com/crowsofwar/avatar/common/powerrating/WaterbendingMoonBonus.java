package com.crowsofwar.avatar.common.powerrating;

import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * Power rating modifier which buffs waterbenders when nighttime
 *
 * @author CrowsOfWar
 */
public class WaterbendingMoonBonus extends PowerRatingModifier {

	@Override
	public double get(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		// Ignore dimensions other than overworld
		if (world.provider.getDimension() != 0) {
			return 0;
		}

		// Number between 0 and 1 based on the current moon stage
		// Greater values mean the moon is fuller
		float moonPhase = world.getCurrentMoonPhaseFactor();

		float nightFactor = world.getSkylightSubtracted() / 11f;
		nightFactor = nightFactor * nightFactor;

		if (world.canBlockSeeSky(entity.getPosition())) {
			return moonPhase * 50 * nightFactor;
		} else {
			return moonPhase * 10 * nightFactor;
		}

	}

}
