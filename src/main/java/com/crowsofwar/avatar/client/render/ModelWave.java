package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Wave beta V1 - talhanation
 * Created using Tabula 5.1.0
 */
public class ModelWave extends ModelBase {
    public ModelRenderer shape;
    public ModelRenderer shapefoam;
    public ModelRenderer shape_1;
    public ModelRenderer shape_2;

    public ModelWave() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.shape_1 = new ModelRenderer(this, 0, 0);
        this.shape_1.setRotationPoint(0.0F, 8.4F, 7.4F);
        this.shape_1.addBox(-12.0F, 0.0F, 1.3F, 24, 6, 14, 0.0F);
        this.setRotateAngle(shape_1, -1.239183768915974F, 0.0F, 0.0F);
        this.shape = new ModelRenderer(this, 0, 0);
        this.shape.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.shape.addBox(-12.0F, 0.0F, 0.4F, 24, 6, 8, 0.0F);
        this.setRotateAngle(shape, -0.3490658503988659F, 0.0F, 0.0F);
        this.shapefoam = new ModelRenderer(this, 90, 70);
        this.shapefoam.setRotationPoint(0.0F, 0.0F, -6.0F);
        this.shapefoam.addBox(-12.0F, 0.2F, -5.6F, 24, 5, 6, 0.0F);
        this.setRotateAngle(shapefoam, 0.24434609527920614F, 0.0F, 0.0F);
        this.shape_2 = new ModelRenderer(this, 0, 0);
        this.shape_2.setRotationPoint(0.0F, 2.0F, 1.0F);
        this.shape_2.addBox(-12.0F, 0.0F, 1.3F, 24, 6, 9, 0.0F);
        this.setRotateAngle(shape_2, -0.8377580409572781F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.shape_1.render(f5);
        this.shape.render(f5);
        this.shapefoam.render(f5);
        this.shape_2.render(f5);
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
