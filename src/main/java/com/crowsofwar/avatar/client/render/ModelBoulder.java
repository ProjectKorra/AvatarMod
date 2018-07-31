package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelEnderCrystal - Either Mojang or a mod author
 * Created using Tabula 6.0.0
 */
public class ModelBoulder extends ModelBase {
    public ModelRenderer cube;

    public ModelBoulder() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.cube = new ModelRenderer(this, 32, 0);
        this.cube.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cube.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 1F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.cube.render(f5);
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
