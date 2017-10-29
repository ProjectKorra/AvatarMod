package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSandstorm - CrowsOfWar
 * Created using Tabula 7.0.0
 */
public class ModelSandstorm extends ModelBase {
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;

    public ModelSandstorm() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.shape3 = new ModelRenderer(this, 40, 0);
        this.shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape3.addBox(-3.0F, 0.0F, -3.0F, 6, 14, 6, 0.0F);
        this.setRotateAngle(shape3, 0.0F, 0.045553093477052F, 0.0F);
        this.shape2 = new ModelRenderer(this, 0, 28);
        this.shape2.setRotationPoint(0.0F, -14.0F, 0.0F);
        this.shape2.addBox(-7.0F, 0.0F, -7.0F, 14, 7, 14, 0.0F);
        this.setRotateAngle(shape2, 0.0F, -0.31869712141416456F, 0.0F);
        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(0.0F, -16.0F, 0.0F);
        this.shape1.addBox(-5.0F, 0.0F, -5.0F, 10, 18, 10, 0.0F);
        this.setRotateAngle(shape1, 0.0F, -0.18203784098300857F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.shape3.render(f5);
        this.shape2.render(f5);
        this.shape1.render(f5);
    }

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float
			netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {

		shape3.rotateAngleY = (float) Math.toRadians(ageInTicks * 20);
		shape1.rotateAngleY = (float) Math.toRadians(ageInTicks * -3);
		shape2.rotateAngleY = (float) Math.toRadians(ageInTicks * 0.5);

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
