package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class LightningChargeHandler extends LightningTickHandler {

	@Override
	@Nullable
	protected AbilityData getLightningData(BendingContext ctx) {
		return ctx.getData().getAbilityData("lightning_arc");
	}
}
