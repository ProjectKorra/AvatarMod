package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityEarthShield;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderEarthShield extends Render<EntityEarthShield> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/earth_shield.png");

	private ModelBase model;

	/**
	 * @param renderManager
	 */
	public RenderEarthShield(RenderManager renderManager) {
		super(renderManager);
		this.model = new ModelEarthspikes();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityEarthShield entity) {
		return TEXTURE;
	}
}
