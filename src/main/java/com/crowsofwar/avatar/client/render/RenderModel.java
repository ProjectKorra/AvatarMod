package com.crowsofwar.avatar.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * An entity render superclass which renders a ModelBase. Since the base Render class doesn't
 * have this functionality (model capabilities are in RenderLivingBase), this is necessary for
 * AvatarEntity renderers.
 *
 * @author CrowsOfWar
 */
public class RenderModel<T extends Entity> extends Render<T> {

	protected ModelBase model;

	public RenderModel(RenderManager renderManager, ModelBase model) {
		super(renderManager);
		this.model = model;
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw,
						 float partialTicks) {

		if (entity.isInvisible()) {
			return;
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(getEntityTexture(entity));
		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		performGlTransforms(entity, x, y, z, entityYaw, partialTicks);
		model.render(entity, 0, 0, entity.ticksExisted + partialTicks, 0, 0, 0.0625f);
		GlStateManager.popMatrix();
		GlStateManager.color(1, 1, 1, 1);

		GlStateManager.disableBlend();

	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(T entity) {
		return null;
	}

	/**
	 * Place to perform all openGL transformations prior to the entity being rendered.
	 */
	protected void performGlTransforms(T entity, double x, double y, double z, float entityYaw,
									 float partialTicks) {

	}

}
