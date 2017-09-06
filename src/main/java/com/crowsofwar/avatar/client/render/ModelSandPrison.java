package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSandPrison - talhanation
 * Created using Tabula 5.1.0
 */
public class ModelSandPrison extends ModelBase {
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;
    public ModelRenderer shape4;
    public ModelRenderer shape5;
    public ModelRenderer shape6;
    public ModelRenderer shape7;
    public ModelRenderer shape8;
    public ModelRenderer shape9;
    public ModelRenderer shape10;
    public ModelRenderer shape11;
    public ModelRenderer shape12;
    public ModelRenderer shape13;
    public ModelRenderer shape14;
    public ModelRenderer shape15;
    public ModelRenderer shape16;

    public ModelSandPrison() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.shape2 = new ModelRenderer(this, 0, 0);
        this.shape2.setRotationPoint(-6.7F, 21.3F, 1.3F);
        this.shape2.addBox(0.9F, -8.9F, -1.3F, 2, 6, 3, 0.0F);
        this.setRotateAngle(shape2, -0.2199114857512855F, -0.024085543677521744F, 1.7133897266828333F);
        this.shape3 = new ModelRenderer(this, 0, 0);
        this.shape3.setRotationPoint(-3.6F, 23.0F, 1.0F);
        this.shape3.addBox(-0.5F, -2.6F, -0.7F, 1, 4, 3, 0.0F);
        this.setRotateAngle(shape3, 0.6283185307179586F, 0.07487462491055674F, -0.239459173373622F);
        this.shape8 = new ModelRenderer(this, 0, 0);
        this.shape8.setRotationPoint(0.0F, 23.0F, -4.0F);
        this.shape8.addBox(2.5F, -5.0F, 0.0F, 1, 5, 3, 0.0F);
        this.setRotateAngle(shape8, -0.4363323129985824F, 0.0F, 0.4886921905584123F);
        this.shape11 = new ModelRenderer(this, 0, 0);
        this.shape11.setRotationPoint(-6.1F, 21.1F, -0.8F);
        this.shape11.addBox(1.6F, -2.0F, -0.6F, 1, 2, 2, 0.0F);
        this.setRotateAngle(shape11, 0.6283185307179586F, -0.18517943363659833F, 0.0153588974175501F);
        this.shape14 = new ModelRenderer(this, 0, 0);
        this.shape14.setRotationPoint(1.0F, 22.6F, 1.3F);
        this.shape14.addBox(0.0F, -1.7F, -0.2F, 2, 2, 1, 0.0F);
        this.setRotateAngle(shape14, 0.0F, 0.0F, -0.4272566008882119F);
        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(-6.6F, 28.3F, 0.2F);
        this.shape1.addBox(-0.4F, -5.2F, -2.9F, 13, 1, 7, 0.0F);
        this.setRotateAngle(shape1, -0.12147491593880534F, -0.2103121748653167F, -0.0F);
        this.shape5 = new ModelRenderer(this, 0, 0);
        this.shape5.setRotationPoint(-7.0F, 28.0F, -5.0F);
        this.shape5.addBox(0.0F, -4.5F, -2.0F, 14, 1, 6, 0.0F);
        this.setRotateAngle(shape5, 0.20943951023931953F, -0.17453292519943295F, -0.0066322511575784525F);
        this.shape7 = new ModelRenderer(this, 0, 0);
        this.shape7.setRotationPoint(-3.0F, 22.0F, -4.0F);
        this.shape7.addBox(1.0F, -5.7F, -1.0F, 2, 6, 3, 0.0F);
        this.setRotateAngle(shape7, -0.22689280275926282F, 0.0F, 1.488765851951163F);
        this.shape6 = new ModelRenderer(this, 0, 0);
        this.shape6.setRotationPoint(-2.4F, 22.4F, 1.2F);
        this.shape6.addBox(0.1F, -2.9F, -1.2F, 1, 2, 2, 0.0F);
        this.setRotateAngle(shape6, 0.5242969072990966F, -1.500459557939525F, -0.13910274138394807F);
        this.shape12 = new ModelRenderer(this, 0, 0);
        this.shape12.setRotationPoint(1.0F, 22.6F, 1.3F);
        this.shape12.addBox(0.0F, 0.0F, 0.0F, 2, 4, 1, 0.0F);
        this.setRotateAngle(shape12, 0.0F, 0.0F, -0.694117443518145F);
        this.shape4 = new ModelRenderer(this, 0, 0);
        this.shape4.setRotationPoint(1.4F, 21.7F, -3.0F);
        this.shape4.addBox(-0.55F, -3.4F, 0.7F, 2, 2, 1, 0.0F);
        this.setRotateAngle(shape4, -0.08203047484373349F, -0.017453292519943295F, 0.027925268031909273F);
        this.shape15 = new ModelRenderer(this, 0, 0);
        this.shape15.setRotationPoint(-2.7F, 24.4F, -2.9F);
        this.shape15.addBox(0.6F, -2.8F, 0.4F, 1, 4, 2, 0.0F);
        this.setRotateAngle(shape15, 0.821526478913731F, -1.4131930953398084F, 0.05637413483941684F);
        this.shape13 = new ModelRenderer(this, 0, 0);
        this.shape13.setRotationPoint(-2.5F, 24.4F, 1.1F);
        this.shape13.addBox(0.6F, -2.9F, 0.4F, 1, 3, 2, 0.0F);
        this.setRotateAngle(shape13, 0.6213372137099813F, -1.4131930953398084F, 0.05637413483941684F);
        this.shape16 = new ModelRenderer(this, 0, 0);
        this.shape16.setRotationPoint(-2.7F, 22.4F, -2.8F);
        this.shape16.addBox(0.7F, -3.0F, -1.6F, 1, 2, 2, 0.0F);
        this.setRotateAngle(shape16, 0.6290166624187563F, -1.4131930953398084F, -0.08325220532012952F);
        this.shape10 = new ModelRenderer(this, 0, 0);
        this.shape10.setRotationPoint(4.9F, 20.3F, -5.1F);
        this.shape10.addBox(0.0F, -4.7F, 3.8F, 1, 3, 2, 0.0F);
        this.setRotateAngle(shape10, -0.5410520681182421F, -0.24434609527920614F, -0.05235987755982988F);
        this.shape9 = new ModelRenderer(this, 0, 0);
        this.shape9.setRotationPoint(1.4F, 21.7F, -3.0F);
        this.shape9.addBox(0.1F, -1.9F, 0.4F, 2, 4, 1, 0.0F);
        this.setRotateAngle(shape9, -0.19722220547535924F, -0.08726646259971647F, -0.3490658503988659F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.shape2.render(f5);
        this.shape3.render(f5);
        this.shape8.render(f5);
        this.shape11.render(f5);
        this.shape14.render(f5);
        this.shape1.render(f5);
        this.shape5.render(f5);
        this.shape7.render(f5);
        this.shape6.render(f5);
        this.shape12.render(f5);
        this.shape4.render(f5);
        this.shape15.render(f5);
        this.shape13.render(f5);
        this.shape16.render(f5);
        this.shape10.render(f5);
        this.shape9.render(f5);
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
