package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
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
        this.textureWidth = 64;
        this.textureHeight = 48;
        this.Spear2 = new ModelRenderer(this, 16, 1);
        this.Spear2.setRotationPoint(0.0F, -1.0F, 0.0F);
        this.Spear2.addBox(-2.0F, -18.0F, -2.0F, 4, 18, 4, 0.0F);
        this.Spear3 = new ModelRenderer(this, 33, 0);
        this.Spear3.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.Spear3.addBox(-2.5F, -10.0F, -2.5F, 5, 10, 5, 0.0F);
        this.Spear1 = new ModelRenderer(this, 3, 2);
        this.Spear1.setRotationPoint(0.0F, 0.1F, 0.0F);
        this.Spear1.addBox(-1.5F, -30.0F, -1.5F, 3, 30, 3, 0.0F);
        this.Spear4 = new ModelRenderer(this, 54, 3);
        this.Spear4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Spear4.addBox(-1.0F, -35.0F, -1.0F, 2, 35, 2, 0.0F);
        this.setRotateAngle(Spear4, 1.5707963267948966F, -0.0F, 0.0F);
        this.Spear1.addChild(this.Spear2);
        this.Spear1.addChild(this.Spear3);
        this.Spear4.addChild(this.Spear1);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.Spear4.render(f5);
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
