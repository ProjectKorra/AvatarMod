package com.crowsofwar.avatar.client.render.iceprison;

import com.crowsofwar.avatar.common.entity.EntityIcePrison;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class RenderIcePrison extends Render<EntityIcePrison> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/ice-prison.png");

	private final ModelBase[] prisonModels;

	public RenderIcePrison(RenderManager renderManager) {
		super(renderManager);
		prisonModels = new ModelBase[]{
				new ModelIcePrison1(),
				new ModelIcePrison2(),
				new ModelIcePrison3(),
				new ModelIcePrison4()
		};
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityIcePrison entity) {
		return TEXTURE;
	}

	private ModelBase getModel(EntityIcePrison entity) {
		double percent = 1 - (double) entity.getImprisonedTime() / entity.getMaxImprisonedTime();
		int index = prisonModels.length - (int) (percent * prisonModels.length);
		if (index == prisonModels.length) {
			index--;
		}
		return prisonModels[index];
	}

	@Override
	public void doRender(EntityIcePrison entity, double x, double y, double z, float entityYaw, float
			partialTicks) {

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.translate(x, y, z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		// Model is upside-down - fix by flipping the model in code
		GlStateManager.scale(1, -1, 1);
		GlStateManager.translate(0, -1.5, 0);

		bindEntityTexture(entity);
		getModel(entity).render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

}
