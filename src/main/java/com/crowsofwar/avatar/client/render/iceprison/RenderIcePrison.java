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
			"textures/entity/IcePrison.png");

	private final ModelBase[] prisonModels;

	public RenderIcePrison(RenderManager renderManager) {
		super(renderManager);
		prisonModels = new ModelBase[] {
				new IcePrisonlvl1(),
				new IcePrisonlvl2(),
				new IcePrisonlvl2v2(),
				new IcePrisonlvl3()
		};
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityIcePrison entity) {
		return TEXTURE;
	}

	private ModelBase getModel(EntityIcePrison entity) {
		double percent = entity.ticksExisted / EntityIcePrison.IMPRISONED_TIME;
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
//		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlpha();
		this.bindEntityTexture(entity);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		getModel(entity).render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

}
