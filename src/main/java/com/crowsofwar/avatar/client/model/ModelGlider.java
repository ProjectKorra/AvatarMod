package com.crowsofwar.avatar.client.model;

import com.crowsofwar.avatar.util.helper.GliderPlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;

/**
 * ModelGlider - DavieDoo
 * Created using Tabula 7.0.1
 */
public class ModelGlider extends ModelBase {

    public ModelRenderer staffMain;
    public ModelRenderer gliderPivotTop;
    public ModelRenderer gliderPivotBottom;
    public ModelRenderer staffMain_WingSupport_Top;
    public ModelRenderer staffMain_WingSupport_Bottom;
    public ModelRenderer wingSupport_right_1;
    public ModelRenderer wingSupport_right_2;
    public ModelRenderer wingSupport_right_3;
    public ModelRenderer wingSupport_left_1;
    public ModelRenderer wingSupport_left_2;
    public ModelRenderer wingSupport_left_3;
    public ModelRenderer wingSupport_left_3_1;
    public ModelRenderer wingSupport_left_3_2;
    public ModelRenderer wingSupport_left_3_3;
    public ModelRenderer wingSupport_left_3_4;
    public ModelRenderer wing_fabric_1;
    public ModelRenderer wing_fabric_2;
    public ModelRenderer wing_fabric_3;
    public ModelRenderer wing_fabric_2_1;
    public ModelRenderer wing_fabric_2_2;
    public ModelRenderer wing_fabric_1_1;
    public ModelRenderer wing_fabric_1_2;
    public ModelRenderer wing_fabric_1_3;
    public ModelRenderer wing_fabric_1_4;
    public ModelRenderer wing_fabric_1_5;

    public ModelGlider() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.gliderPivotBottom = new ModelRenderer(this, 47, 6);
        this.gliderPivotBottom.setRotationPoint(0.0F, 10.6F, -1.0F);
        this.gliderPivotBottom.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(gliderPivotBottom, 0.0F, 0.0F, 0.7853981633974483F);
        this.wing_fabric_1_1 = new ModelRenderer(this, 0, 43);
        this.wing_fabric_1_1.setRotationPoint(3.8F, 11.8F, 0.0F);
        this.wing_fabric_1_1.addBox(0.0F, 0.0F, 0.0F, 4, 3, 0, 0.0F);
        this.setRotateAngle(wing_fabric_1_1, 0.0F, 0.0F, 0.05235987755982988F);
        this.wingSupport_left_3 = new ModelRenderer(this, 0, 39);
        this.wingSupport_left_3.setRotationPoint(0.0F, -4.100000000000002F, 0.0F);
        this.wingSupport_left_3.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_left_3, 3.141592653589793F, 0.0F, 1.9198621771937625F);
        this.wingSupport_left_3_3 = new ModelRenderer(this, 0, 39);
        this.wingSupport_left_3_3.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.wingSupport_left_3_3.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_left_3_3, 0.0F, 0.0F, 1.3089969389957472F);
        this.wing_fabric_1_2 = new ModelRenderer(this, 0, 43);
        this.wing_fabric_1_2.setRotationPoint(5.7F, 12.7F, 0.0F);
        this.wing_fabric_1_2.addBox(0.0F, 0.0F, 0.0F, 3, 5, 0, 0.0F);
        this.setRotateAngle(wing_fabric_1_2, 0.0F, 0.0F, 0.8651597102135892F);
        this.wingSupport_left_3_2 = new ModelRenderer(this, 0, 39);
        this.wingSupport_left_3_2.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.wingSupport_left_3_2.addBox(0.0F, 0.0F, 0.0F, 7, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_left_3_2, 3.141592653589793F, 0.0F, 2.356194490192345F);
        this.wingSupport_left_1 = new ModelRenderer(this, 0, 39);
        this.wingSupport_left_1.setRotationPoint(0.0F, -4.100000000000002F, 0.0F);
        this.wingSupport_left_1.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_left_1, 3.141592653589793F, 0.0F, 2.705260340591211F);
        this.wingSupport_right_2 = new ModelRenderer(this, 0, 39);
        this.wingSupport_right_2.setRotationPoint(0.0F, -4.100000000000002F, 0.0F);
        this.wingSupport_right_2.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_right_2, 0.0F, 0.0F, 0.8726646259971648F);
        this.wingSupport_left_3_1 = new ModelRenderer(this, 0, 39);
        this.wingSupport_left_3_1.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.wingSupport_left_3_1.addBox(0.0F, 0.0F, 0.0F, 7, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_left_3_1, 0.0F, 0.0F, 0.7853981633974483F);
        this.wing_fabric_2_2 = new ModelRenderer(this, 0, 52);
        this.wing_fabric_2_2.mirror = true;
        this.wing_fabric_2_2.setRotationPoint(6.0F, 0.7F, 0.0F);
        this.wing_fabric_2_2.addBox(0.0F, 0.0F, 0.0F, 6, 12, 0, 0.0F);
        this.setRotateAngle(wing_fabric_2_2, 0.0F, 0.0F, 1.5707963267948966F);
        this.staffMain = new ModelRenderer(this, 59, 0);
        this.staffMain.setRotationPoint(-0.5F, -12.300000000000002F, -0.5F);
        this.staffMain.addBox(0.0F, 0.0F, 0.0F, 1, 34, 1, 0.0F);
        this.staffMain_WingSupport_Top = new ModelRenderer(this, 0, 39);
        this.staffMain_WingSupport_Top.setRotationPoint(-16.0F, -4.8F, -0.4F);
        this.staffMain_WingSupport_Top.addBox(0.0F, 0.0F, 0.0F, 32, 1, 1, 0.0F);
        this.wingSupport_right_1 = new ModelRenderer(this, 0, 39);
        this.wingSupport_right_1.setRotationPoint(0.0F, -4.100000000000002F, 0.0F);
        this.wingSupport_right_1.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_right_1, 0.0F, 0.0F, 0.4363323129985824F);
        this.wingSupport_right_3 = new ModelRenderer(this, 0, 39);
        this.wingSupport_right_3.setRotationPoint(0.0F, -4.100000000000002F, 0.0F);
        this.wingSupport_right_3.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_right_3, 0.0F, 0.0F, 1.186823891356144F);
        this.gliderPivotTop = new ModelRenderer(this, 47, 6);
        this.gliderPivotTop.setRotationPoint(0.0F, -5.400000000000002F, -1.0F);
        this.gliderPivotTop.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(gliderPivotTop, 0.0F, 0.0F, 0.7853981633974483F);
        this.wing_fabric_2 = new ModelRenderer(this, 0, 52);
        this.wing_fabric_2.mirror = true;
        this.wing_fabric_2.setRotationPoint(12.2F, -4.3F, 0.0F);
        this.wing_fabric_2.addBox(0.0F, 0.0F, 0.0F, 6, 11, 0, 0.0F);
        this.setRotateAngle(wing_fabric_2, 0.0F, 0.0F, 1.0129890978575087F);
        this.wing_fabric_3 = new ModelRenderer(this, 0, 43);
        this.wing_fabric_3.setRotationPoint(-15.7F, -4.1F, 0.0F);
        this.wing_fabric_3.addBox(0.0F, 0.0F, 0.0F, 7, 5, 0, 0.0F);
        this.setRotateAngle(wing_fabric_3, 0.0F, 0.0F, -0.08726646259971647F);
        this.wing_fabric_1_4 = new ModelRenderer(this, 0, 43);
        this.wing_fabric_1_4.setRotationPoint(-7.6F, 14.8F, 0.0F);
        this.wing_fabric_1_4.addBox(0.0F, 0.0F, 0.0F, 3, 5, 0, 0.0F);
        this.setRotateAngle(wing_fabric_1_4, 0.0F, 0.0F, -0.8285077959217081F);
        this.wingSupport_left_2 = new ModelRenderer(this, 0, 39);
        this.wingSupport_left_2.setRotationPoint(0.0F, -4.100000000000002F, 0.0F);
        this.wingSupport_left_2.addBox(0.0F, 0.0F, 0.0F, 11, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_left_2, 3.141592653589793F, 0.0F, 2.2689280275926285F);
        this.wing_fabric_1 = new ModelRenderer(this, 0, 43);
        this.wing_fabric_1.setRotationPoint(8.8F, -4.8F, 0.0F);
        this.wing_fabric_1.addBox(0.0F, 0.0F, 0.0F, 7, 5, 0, 0.0F);
        this.setRotateAngle(wing_fabric_1, 0.0F, 0.0F, 0.08726646259971647F);
        this.wing_fabric_2_1 = new ModelRenderer(this, 0, 52);
        this.wing_fabric_2_1.mirror = true;
        this.wing_fabric_2_1.setRotationPoint(-15.3F, 0.9F, 0.0F);
        this.wing_fabric_2_1.addBox(0.0F, 0.0F, 0.0F, 6, 11, 0, 0.0F);
        this.setRotateAngle(wing_fabric_2_1, 0.0F, 0.0F, -1.0129890978575087F);
        this.wing_fabric_1_5 = new ModelRenderer(this, 0, 43);
        this.wing_fabric_1_5.setRotationPoint(-4.0F, 15.2F, 0.0F);
        this.wing_fabric_1_5.addBox(0.0F, 0.0F, 0.0F, 8, 3, 0, 0.0F);
        this.wing_fabric_1_3 = new ModelRenderer(this, 0, 43);
        this.wing_fabric_1_3.setRotationPoint(-7.7F, 11.8F, 0.0F);
        this.wing_fabric_1_3.addBox(0.0F, 0.0F, 0.0F, 4, 3, 0, 0.0F);
        this.setRotateAngle(wing_fabric_1_3, 0.0F, 0.0F, -0.05235987755982988F);
        this.staffMain_WingSupport_Bottom = new ModelRenderer(this, 0, 39);
        this.staffMain_WingSupport_Bottom.setRotationPoint(-8.0F, 11.2F, -0.4F);
        this.staffMain_WingSupport_Bottom.addBox(0.0F, 0.0F, 0.0F, 16, 1, 1, 0.0F);
        this.wingSupport_left_3_4 = new ModelRenderer(this, 0, 39);
        this.wingSupport_left_3_4.setRotationPoint(0.0F, 11.0F, 0.0F);
        this.wingSupport_left_3_4.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
        this.setRotateAngle(wingSupport_left_3_4, 3.141592653589793F, 0.0F, 1.8325957145940461F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.gliderPivotBottom.render(f5);
        this.wing_fabric_1_1.render(f5);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_left_3.offsetX, this.wingSupport_left_3.offsetY, this.wingSupport_left_3.offsetZ);
        GlStateManager.translate(this.wingSupport_left_3.rotationPointX * f5, this.wingSupport_left_3.rotationPointY * f5, this.wingSupport_left_3.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_left_3.offsetX, -this.wingSupport_left_3.offsetY, -this.wingSupport_left_3.offsetZ);
        GlStateManager.translate(-this.wingSupport_left_3.rotationPointX * f5, -this.wingSupport_left_3.rotationPointY * f5, -this.wingSupport_left_3.rotationPointZ * f5);
        this.wingSupport_left_3.render(f5);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_left_3_3.offsetX, this.wingSupport_left_3_3.offsetY, this.wingSupport_left_3_3.offsetZ);
        GlStateManager.translate(this.wingSupport_left_3_3.rotationPointX * f5, this.wingSupport_left_3_3.rotationPointY * f5, this.wingSupport_left_3_3.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_left_3_3.offsetX, -this.wingSupport_left_3_3.offsetY, -this.wingSupport_left_3_3.offsetZ);
        GlStateManager.translate(-this.wingSupport_left_3_3.rotationPointX * f5, -this.wingSupport_left_3_3.rotationPointY * f5, -this.wingSupport_left_3_3.rotationPointZ * f5);
        this.wingSupport_left_3_3.render(f5);
        GlStateManager.popMatrix();
        this.wing_fabric_1_2.render(f5);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_left_3_2.offsetX, this.wingSupport_left_3_2.offsetY, this.wingSupport_left_3_2.offsetZ);
        GlStateManager.translate(this.wingSupport_left_3_2.rotationPointX * f5, this.wingSupport_left_3_2.rotationPointY * f5, this.wingSupport_left_3_2.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_left_3_2.offsetX, -this.wingSupport_left_3_2.offsetY, -this.wingSupport_left_3_2.offsetZ);
        GlStateManager.translate(-this.wingSupport_left_3_2.rotationPointX * f5, -this.wingSupport_left_3_2.rotationPointY * f5, -this.wingSupport_left_3_2.rotationPointZ * f5);
        this.wingSupport_left_3_2.render(f5);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_left_1.offsetX, this.wingSupport_left_1.offsetY, this.wingSupport_left_1.offsetZ);
        GlStateManager.translate(this.wingSupport_left_1.rotationPointX * f5, this.wingSupport_left_1.rotationPointY * f5, this.wingSupport_left_1.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_left_1.offsetX, -this.wingSupport_left_1.offsetY, -this.wingSupport_left_1.offsetZ);
        GlStateManager.translate(-this.wingSupport_left_1.rotationPointX * f5, -this.wingSupport_left_1.rotationPointY * f5, -this.wingSupport_left_1.rotationPointZ * f5);
        this.wingSupport_left_1.render(f5);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_right_2.offsetX, this.wingSupport_right_2.offsetY, this.wingSupport_right_2.offsetZ);
        GlStateManager.translate(this.wingSupport_right_2.rotationPointX * f5, this.wingSupport_right_2.rotationPointY * f5, this.wingSupport_right_2.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_right_2.offsetX, -this.wingSupport_right_2.offsetY, -this.wingSupport_right_2.offsetZ);
        GlStateManager.translate(-this.wingSupport_right_2.rotationPointX * f5, -this.wingSupport_right_2.rotationPointY * f5, -this.wingSupport_right_2.rotationPointZ * f5);
        this.wingSupport_right_2.render(f5);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_left_3_1.offsetX, this.wingSupport_left_3_1.offsetY, this.wingSupport_left_3_1.offsetZ);
        GlStateManager.translate(this.wingSupport_left_3_1.rotationPointX * f5, this.wingSupport_left_3_1.rotationPointY * f5, this.wingSupport_left_3_1.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_left_3_1.offsetX, -this.wingSupport_left_3_1.offsetY, -this.wingSupport_left_3_1.offsetZ);
        GlStateManager.translate(-this.wingSupport_left_3_1.rotationPointX * f5, -this.wingSupport_left_3_1.rotationPointY * f5, -this.wingSupport_left_3_1.rotationPointZ * f5);
        this.wingSupport_left_3_1.render(f5);
        GlStateManager.popMatrix();
        this.wing_fabric_2_2.render(f5);
        this.staffMain.render(f5);
        this.staffMain_WingSupport_Top.render(f5);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_right_1.offsetX, this.wingSupport_right_1.offsetY, this.wingSupport_right_1.offsetZ);
        GlStateManager.translate(this.wingSupport_right_1.rotationPointX * f5, this.wingSupport_right_1.rotationPointY * f5, this.wingSupport_right_1.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_right_1.offsetX, -this.wingSupport_right_1.offsetY, -this.wingSupport_right_1.offsetZ);
        GlStateManager.translate(-this.wingSupport_right_1.rotationPointX * f5, -this.wingSupport_right_1.rotationPointY * f5, -this.wingSupport_right_1.rotationPointZ * f5);
        this.wingSupport_right_1.render(f5);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_right_3.offsetX, this.wingSupport_right_3.offsetY, this.wingSupport_right_3.offsetZ);
        GlStateManager.translate(this.wingSupport_right_3.rotationPointX * f5, this.wingSupport_right_3.rotationPointY * f5, this.wingSupport_right_3.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_right_3.offsetX, -this.wingSupport_right_3.offsetY, -this.wingSupport_right_3.offsetZ);
        GlStateManager.translate(-this.wingSupport_right_3.rotationPointX * f5, -this.wingSupport_right_3.rotationPointY * f5, -this.wingSupport_right_3.rotationPointZ * f5);
        this.wingSupport_right_3.render(f5);
        GlStateManager.popMatrix();
        this.gliderPivotTop.render(f5);
        this.wing_fabric_2.render(f5);
        this.wing_fabric_3.render(f5);
        this.wing_fabric_1_4.render(f5);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_left_2.offsetX, this.wingSupport_left_2.offsetY, this.wingSupport_left_2.offsetZ);
        GlStateManager.translate(this.wingSupport_left_2.rotationPointX * f5, this.wingSupport_left_2.rotationPointY * f5, this.wingSupport_left_2.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_left_2.offsetX, -this.wingSupport_left_2.offsetY, -this.wingSupport_left_2.offsetZ);
        GlStateManager.translate(-this.wingSupport_left_2.rotationPointX * f5, -this.wingSupport_left_2.rotationPointY * f5, -this.wingSupport_left_2.rotationPointZ * f5);
        this.wingSupport_left_2.render(f5);
        GlStateManager.popMatrix();
        this.wing_fabric_1.render(f5);
        this.wing_fabric_2_1.render(f5);
        this.wing_fabric_1_5.render(f5);
        this.wing_fabric_1_3.render(f5);
        this.staffMain_WingSupport_Bottom.render(f5);
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.wingSupport_left_3_4.offsetX, this.wingSupport_left_3_4.offsetY, this.wingSupport_left_3_4.offsetZ);
        GlStateManager.translate(this.wingSupport_left_3_4.rotationPointX * f5, this.wingSupport_left_3_4.rotationPointY * f5, this.wingSupport_left_3_4.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.5D, 0.5D);
        GlStateManager.translate(-this.wingSupport_left_3_4.offsetX, -this.wingSupport_left_3_4.offsetY, -this.wingSupport_left_3_4.offsetZ);
        GlStateManager.translate(-this.wingSupport_left_3_4.rotationPointX * f5, -this.wingSupport_left_3_4.rotationPointY * f5, -this.wingSupport_left_3_4.rotationPointZ * f5);
        this.wingSupport_left_3_4.render(f5);
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

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        //Handles gliders scale rotation and translation for third person perspective
        GlStateManager.translate(0, 0.2f, 0); //move to on the back (quite close)
        GlStateManager.rotate(90, 1.0F, 0.0F, 0.0F);

        if (!GliderPlayerHelper.shouldBeGliding(Minecraft.getMinecraft().player))
        {
            GlStateManager.scale(1.1, 1.1, 1.2); //scale slightly larger
        }

    }
}
