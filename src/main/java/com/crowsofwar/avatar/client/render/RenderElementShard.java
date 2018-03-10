package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityElementshard;
import com.crowsofwar.avatar.common.entity.EntityIceShard;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderElementShard extends RenderModel<EntityElementshard> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/ice-shard.png");

	public RenderElementShard(RenderManager renderManager) {
		super(renderManager, new ModelIceShard());
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityElementshard entity) {
		return TEXTURE;
	}

	@Override
	protected void performGlTransforms(EntityElementshard entity, double x, double y, double z, float entityYaw, float partialTicks) {
		// Should be rotating in degrees here...?
		// radians doesn't work
		GlStateManager.rotate(-entity.rotationYaw, 0, 1, 0);
		GlStateManager.rotate(entity.rotationPitch, 1, 0, 0);
		GlStateManager.rotate(entity.ticksExisted * entity.getRotationSpeed(), 0, 0, 1);
	}
}

