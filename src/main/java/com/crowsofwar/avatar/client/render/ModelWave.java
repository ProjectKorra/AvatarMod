package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Wave - talhanation
 * Created using Tabula 5.1.0
 */
public class ModelWave extends ModelBase {
	public ModelRenderer shape;
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape5;

	public ModelWave() {
		this.textureWidth = 256;
		this.textureHeight = 128;
		this.shape = new ModelRenderer(this, 0, 0);
		this.shape.setRotationPoint(0.0F, 19.5F, 3.6F);
		this.shape.addBox(-15.0F, 0.0F, 5.0F, 30, 5, 10, 0.0F);
		this.setRotateAngle(shape, -0.15707963267948966F, 0.0F, 0.0F);
		this.shape3 = new ModelRenderer(this, 0, 0);
		this.shape3.setRotationPoint(0.0F, 7.4F, 4.4F);
		this.shape3.addBox(-15.0F, 0.0F, 1.3F, 30, 6, 9, 0.0F);
		this.setRotateAngle(shape3, -1.239183768915974F, 0.0F, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(0.0F, -1.0F, -9.0F);
		this.shape1.addBox(-15.0F, 0.0F, 0.4F, 30, 6, 8, 0.0F);
		this.setRotateAngle(shape1, -0.3490658503988659F, 0.0F, 0.0F);
		this.shape2 = new ModelRenderer(this, 0, 0);
		this.shape2.setRotationPoint(0.0F, -1.0F, -9.0F);
		this.shape2.addBox(-15.0F, 0.2F, -5.6F, 30, 5, 6, 0.0F);
		this.setRotateAngle(shape2, 0.24434609527920614F, 0.0F, 0.0F);
		this.shape5 = new ModelRenderer(this, 0, 0);
		this.shape5.setRotationPoint(0.0F, 13.0F, 4.0F);
		this.shape5.addBox(-15.0F, -0.5F, 3.3F, 30, 6, 8, 0.0F);
		this.setRotateAngle(shape5, -0.8203047484373349F, 0.0F, 0.0F);
		this.shape4 = new ModelRenderer(this, 0, 0);
		this.shape4.setRotationPoint(0.0F, 1.0F, -2.0F);
		this.shape4.addBox(-15.0F, 0.0F, 1.3F, 30, 6, 9, 0.0F);
		this.setRotateAngle(shape4, -0.8377580409572781F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.shape.render(f5);
		this.shape3.render(f5);
		this.shape1.render(f5);
		this.shape2.render(f5);
		this.shape5.render(f5);
		this.shape4.render(f5);
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
