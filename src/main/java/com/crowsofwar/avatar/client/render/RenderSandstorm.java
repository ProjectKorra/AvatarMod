package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntitySandstorm;
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

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySandstorm entity) {
		return TEXTURE;
	}
}
