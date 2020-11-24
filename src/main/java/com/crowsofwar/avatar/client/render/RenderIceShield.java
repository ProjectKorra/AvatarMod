package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntityIceShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class RenderIceShield extends RenderModel<EntityIceShield> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/ice-shield.png");

	public RenderIceShield(RenderManager renderManager) {
		super(renderManager, new ModelIceShield());
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityIceShield entity) {
		return TEXTURE;
	}

	@Override
	protected void performGlTransforms(EntityIceShield entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.translate(0, 0.3, 0);
	}
}
