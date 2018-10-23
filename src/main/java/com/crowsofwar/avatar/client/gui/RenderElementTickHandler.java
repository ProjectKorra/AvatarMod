package com.crowsofwar.avatar.client.gui;

import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class RenderElementTickHandler extends TickHandler {

	public RenderElementTickHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		int duration = ctx.getData().getTickHandlerDuration(this);
		return duration >= 200;
		//return duration >= CLIENT_CONFIG.activeBendingSettings.bendingMenuDuration;
	}
}
