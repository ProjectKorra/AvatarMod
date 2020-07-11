package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntityAirGust;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderAirGust extends Render<EntityAirGust> {

	public RenderAirGust(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityAirGust entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityAirGust entity) {
		return null;
	}
}
