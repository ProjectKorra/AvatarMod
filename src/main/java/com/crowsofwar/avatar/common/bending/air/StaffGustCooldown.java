package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class StaffGustCooldown extends TickHandler {

	public static TickHandler STAFF_GUST_HANDLER = new StaffGustCooldown();
	@Override
	public boolean tick(BendingContext ctx) {
		return ctx.getData().getTickHandlerDuration(this) >= 150;
	}
}
