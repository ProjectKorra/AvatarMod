package com.crowsofwar.avatar.client.gui;

import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;

public class RenderElementHandler extends TickHandler {

	public RenderElementHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		int duration = ctx.getData().getTickHandlerDuration(this);
		return duration >= CLIENT_CONFIG.activeBendingSettings.bendingMenuDuration;
	}
}
