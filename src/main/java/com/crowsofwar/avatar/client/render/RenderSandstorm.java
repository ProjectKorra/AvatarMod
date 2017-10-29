package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class RenderSandstorm extends RenderModel<EntitySandstorm> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/sandstorm.png");

	public RenderSandstorm(RenderManager renderManager) {
		super(renderManager, new ModelSandstorm());
	}

	@Override
	protected void performGlTransforms(EntitySandstorm entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.translate(0, -2.5, 0);
		GlStateManager.scale(3, 3, 3);


	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySandstorm entity) {
		return TEXTURE;
	}
}
