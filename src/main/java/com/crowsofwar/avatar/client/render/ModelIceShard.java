package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

/**
 * IceShard - CrowsOfWar<br />
 * Created using Tabula 5.1.0
 *
 * @author model & code created by CrowsOfWar
 */
public class ModelIceShard extends ModelBase {

	private static final int TEXTURE_SCALE = 3;

	public ModelRenderer spike;
	public ModelRenderer bulb;

	public ModelIceShard() {

		this.textureWidth = 64;
		this.textureHeight = 32;
		this.bulb = new ModelRenderer(this, 0, 0);
		this.bulb.setRotationPoint(-0.15F, -0.15F, 1.0F);
		this.bulb.addBox(0.0F, 0.0F, 0.0F, 2 * TEXTURE_SCALE, 2 * TEXTURE_SCALE, 3 * TEXTURE_SCALE, 0.0F);
		this.spike = new ModelRenderer(this, 0, 0);
		this.spike.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.spike.addBox(0.0F, 0.0F, 0.0F, 1 * TEXTURE_SCALE, 1 * TEXTURE_SCALE, 6 * TEXTURE_SCALE, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {

		GlStateManager.pushMatrix();
		GlStateManager.scale(1f / TEXTURE_SCALE, 1f / TEXTURE_SCALE, 1f / TEXTURE_SCALE);

		GlStateManager.pushMatrix();
		GlStateManager.translate(this.bulb.offsetX, this.bulb.offsetY, this.bulb.offsetZ);
		GlStateManager.translate(this.bulb.rotationPointX * f5, this.bulb.rotationPointY * f5,
				this.bulb.rotationPointZ * f5);
		GlStateManager.scale(0.7D, 0.7D, 1.0D);
		GlStateManager.translate(-this.bulb.offsetX, -this.bulb.offsetY, -this.bulb.offsetZ);
		GlStateManager.translate(-this.bulb.rotationPointX * f5, -this.bulb.rotationPointY * f5,
				-this.bulb.rotationPointZ * f5);
		this.bulb.render(f5);
		GlStateManager.popMatrix();
		this.spike.render(f5);

		GlStateManager.popMatrix();

	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
