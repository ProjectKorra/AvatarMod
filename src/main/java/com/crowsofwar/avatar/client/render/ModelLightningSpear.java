package com.crowsofwar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * LightningSpear - FavouriteDraogn
 * Created using Tabula 6.0.0
 */
public class ModelLightningSpear extends ModelBase {
    public ModelRenderer shape1;

    public ModelLightningSpear() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.shape1 = new ModelRenderer(this, 4, 4);
        this.shape1.setRotationPoint(-6.2F, 4.0F, -0.8F);
        this.shape1.addBox(0.0F, 0.0F, 0.0F, 13, 2, 2, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.shape1.render(f5);
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
