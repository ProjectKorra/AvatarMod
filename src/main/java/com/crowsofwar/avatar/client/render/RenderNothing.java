package com.crowsofwar.avatar.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Dummy renderer for an entity which doesn't render at all
 *
 * @author CrowsOfWar
 */
public class RenderNothing<T extends Entity> extends Render<Entity> {

	public RenderNothing(RenderManager renderManager) {
		super(renderManager);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

}
