package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityBoulder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderBoulder extends Render<EntityBoulder> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/earth_shield.png");

	private ModelBase model;

	/**
	 * @param renderManager
	 */
	public RenderBoulder(RenderManager renderManager) {
		super(renderManager);
		this.model = new ModelBoulder();
	}
	@Override
	public void doRender(EntityBoulder entity, double x, double y, double z, float entityYaw,
						 float partialTicks) {

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		model.render(entity, 0, 0, 0, 0, 0, entity.getSize());
		GlStateManager.popMatrix();

		GlStateManager.disableBlend();

	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBoulder entity) {
		return TEXTURE;
	}
}
