package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.LIGHTNING_CHARGE;

/**
 * @author CrowsOfWar
 */
public class AbilityLightningArc extends Ability {

	public AbilityLightningArc() {
		super(Lightningbending.ID, "lightning_arc");
	}

	@Override
	public void execute(AbilityContext ctx) {

		Bender bender = ctx.getBender();
		BendingData data = ctx.getData();

		boolean hasChi = bender.consumeChi(STATS_CONFIG.chiLightning);
		boolean hasLightningCharge = data.hasTickHandler(LIGHTNING_CHARGE);

		if (hasChi && !hasLightningCharge) {
			ctx.getData().addTickHandler(LIGHTNING_CHARGE);
		}

	}

	@Override
	public int getBaseTier() {
		return 2;
	}

	@Override
	public int getBaseParentTier() {
		return 4;
	}
}
