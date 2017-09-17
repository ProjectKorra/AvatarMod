package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityIceShield;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class RenderIceShield extends Render<EntityIceShield> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/ice-shield.png");

	private ModelBase model;

	public RenderIceShield(RenderManager renderManager) {
		super(renderManager);
		model = new ModelIceShield();
	}

	@Override
	public void doRender(EntityIceShield entity, double x, double y, double z, float entityYaw,
						 float partialTicks) {

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		GlStateManager.enableBlend();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(0, 0.3, 0);
		model.render(entity, 0, 0, 0, 0, 0, 0.0625f);
		GlStateManager.popMatrix();

		GlStateManager.disableBlend();

	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityIceShield entity) {
		return TEXTURE;
	}

}
