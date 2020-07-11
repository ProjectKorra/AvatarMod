package com.crowsofwar.avatar.bending.bendingmultipliers;

import com.crowsofwar.avatar.util.data.PowerRatingModifier;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
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
		EntityLivingBase entity = ctx.getBenderEntity();

		// Ignore dimensions other than the overworld
		if (entity.world.provider.getDimension() != 0) {
			return 0;
		}

		int reduce = world.getSkylightSubtracted();
		if (entity.world.isRainingAt(entity.getPosition())) {
			reduce += 3;
		}
		if (entity.world.isThundering()) {
			reduce += 2;
		}

		return 20 - reduce * (70 / 11.0);

	}

}
