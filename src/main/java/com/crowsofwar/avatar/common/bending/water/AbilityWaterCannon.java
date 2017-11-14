package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityWaterCannon extends Ability{
	public AbilityWaterCannon() {
		super(Waterbending.ID, "water_cannon");
		requireRaytrace(-1, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();
		BendingData data = ctx.getData();

		boolean hasChi = bender.consumeChi(STATS_CONFIG.chiWaterCannon);
		boolean hasWaterCharge = data.hasTickHandler(TickHandler.WATER_CHARGE);

		if (ctx.consumeWater(3)) {
			if (hasChi && !hasWaterCharge) {
				ctx.getData().addTickHandler(TickHandler.WATER_CHARGE);
			}
		}
	}

}

