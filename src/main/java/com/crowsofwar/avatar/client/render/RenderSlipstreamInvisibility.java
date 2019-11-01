package com.crowsofwar.avatar.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class RenderSlipstreamInvisibility extends RenderPlayer {

	public RenderSlipstreamInvisibility(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
}
