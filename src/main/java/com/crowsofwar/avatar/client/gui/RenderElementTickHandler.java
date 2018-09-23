package com.crowsofwar.avatar.client.gui;

import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.TickHandlerController;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;

public class RenderElementTickHandler extends TickHandler {
	public static TickHandler RENDER_ELEMENT_HANDLER = TickHandlerController.fromId(TickHandlerController.RENDER_ELEMENT_HANDLER_ID);

	public RenderElementTickHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		int duration = ctx.getData().getTickHandlerDuration(this);
		return duration >= CLIENT_CONFIG.activeBendingSettings.bendingMenuDuration;
	}
}
