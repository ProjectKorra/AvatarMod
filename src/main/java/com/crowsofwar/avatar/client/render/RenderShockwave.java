package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

public class RenderShockwave extends Render<EntityShockwave> {

	public RenderShockwave(RenderManager renderManager) {
		super(renderManager);
	}


	@ParametersAreNonnullByDefault
	@Override
	public void doRender(EntityShockwave entity, double x, double y, double z, float entityYaw, float partialTicks) {
	}

	@ParametersAreNonnullByDefault
	@Override
	protected ResourceLocation getEntityTexture(EntityShockwave entity) {
		return null;
	}
}
