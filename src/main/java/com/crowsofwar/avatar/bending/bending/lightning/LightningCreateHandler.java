package com.crowsofwar.avatar.bending.bending.lightning;

import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class LightningCreateHandler extends LightningChargeHandler {

	public LightningCreateHandler(int id) {
		super(id);
	}

	@Override
	@Nullable
	protected AbilityData getLightningData(BendingContext ctx) {
		return ctx.getData().getAbilityData("lightning_arc");
	}
}
