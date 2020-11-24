package com.crowsofwar.avatar.bending.bending.air.tickhandlers;

import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

public class StaffGustCooldown extends TickHandler {

	public StaffGustCooldown(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		return ctx.getData().getTickHandlerDuration(this) >= 150;
	}
}
