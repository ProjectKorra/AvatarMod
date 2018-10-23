package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.*;
import net.minecraft.entity.Entity;

/**
 * Spear - Undefined
 * Created using Tabula 5.1.0
 */
public class ModelLightningSpear extends ModelBase {
	public ModelRenderer Spear4;
	public ModelRenderer Spear1;
	public ModelRenderer Spear2;
	public ModelRenderer Spear3;

	public ModelLightningSpear() {
		textureWidth = 64;
		textureHeight = 48;
		Spear2 = new ModelRenderer(this, 16, 1);
		Spear2.setRotationPoint(0.0F, -1.0F, 0.0F);
		Spear2.addBox(-2.0F, -18.0F, -2.0F, 4, 18, 4, 0.0F);
		Spear3 = new ModelRenderer(this, 33, 0);
		Spear3.setRotationPoint(0.0F, -2.0F, 0.0F);
		Spear3.addBox(-2.5F, -10.0F, -2.5F, 5, 10, 5, 0.0F);
		Spear1 = new ModelRenderer(this, 3, 2);
		Spear1.setRotationPoint(0.0F, 0.1F, 0.0F);
		Spear1.addBox(-1.5F, -30.0F, -1.5F, 3, 30, 3, 0.0F);
		Spear4 = new ModelRenderer(this, 54, 3);
		Spear4.setRotationPoint(0.0F, 0.0F, 0.0F);
		Spear4.addBox(-1.0F, -35.0F, -1.0F, 2, 35, 2, 0.0F);
		setRotateAngle(Spear4, 1.5707963267948966F, -0.0F, 0.0F);
		Spear1.addChild(Spear2);
		Spear1.addChild(Spear3);
		Spear4.addChild(Spear1);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Spear4.render(f5);
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
