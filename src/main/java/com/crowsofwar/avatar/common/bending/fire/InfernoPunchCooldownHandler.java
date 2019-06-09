package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class InfernoPunchCooldownHandler extends TickHandler {

	public InfernoPunchCooldownHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		return ctx.getData().getTickHandlerDuration(this) >= 7;
	}
}
