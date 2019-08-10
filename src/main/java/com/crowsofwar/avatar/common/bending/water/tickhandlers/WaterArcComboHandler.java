package com.crowsofwar.avatar.common.bending.water.tickhandlers;

import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class WaterArcComboHandler extends TickHandler {

	public WaterArcComboHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		return ctx.getData().getTickHandlerDuration(this) >= 15;
	}
}
