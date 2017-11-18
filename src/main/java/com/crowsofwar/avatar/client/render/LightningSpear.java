package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * LightningSpear - CrowsOfWar
 * Created using Tabula 7.0.0
 */
public class LightningSpear extends ModelBase {
    public ModelRenderer shape1;
    public ModelRenderer shape2;

    public LightningSpear() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape1.addBox(0.0F, 0.0F, 0.0F, 2, 2, 16, 0.0F);
        this.shape2 = new ModelRenderer(this, 0, 18);
        this.shape2.setRotationPoint(-1.0F, -1.0F, -2.0F);
        this.shape2.addBox(0.0F, 0.0F, 0.0F, 4, 4, 20, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.shape1.render(f5);
        this.shape2.render(f5);
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
