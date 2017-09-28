package com.crowsofwar.avatar.common.powerrating;

import com.crowsofwar.avatar.common.data.PowerRatingModifier;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.world.World;

/**
 * Power rating modifier which buffs/reduces firebender power in the sun
 *
 * @author CrowsOfWar
 */
public class FirebendingSunModifier extends PowerRatingModifier {

	@Override
	public double get(BendingContext ctx) {

		World world = ctx.getWorld();

		// Ignore dimensions other than the overworld
		if (world.provider.getDimension() != 0) {
			return 0;
		}

		int reduce = world.getSkylightSubtracted();
		if (world.isRaining()) {
			reduce += 3;
		}
		if (world.isThundering()) {
			reduce += 2;
		}

		return 50 - reduce * (50 / 7.0);

	}

}
