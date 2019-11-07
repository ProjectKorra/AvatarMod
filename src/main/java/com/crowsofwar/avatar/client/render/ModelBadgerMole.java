package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.mob.EntityBadgerMole;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import static java.lang.Math.cos;

/**
 * BadgerMole - talhanation
 * Created using Tabula 5.1.0
 */
public class ModelBadgerMole extends ModelBase {
    public ModelRenderer LegLB;
    public ModelRenderer tail1;
    public ModelRenderer shinLB;
    public ModelRenderer footLB;
    public ModelRenderer claw1lb;
    public ModelRenderer claw2lb;
    public ModelRenderer claw3lb;
    public ModelRenderer claw4lb;
    public ModelRenderer claw5lb;
    public ModelRenderer tail2;
    public ModelRenderer Head;
    public ModelRenderer Body;
    public ModelRenderer body1;
    public ModelRenderer body4;
    public ModelRenderer body5;
    public ModelRenderer LegRB;
    public ModelRenderer LegRF;
    public ModelRenderer LegLF;
    public ModelRenderer body2;
    public ModelRenderer body2_1;
    public ModelRenderer body6;
    public ModelRenderer body6_1;
    public ModelRenderer body2_2;
    public ModelRenderer face1;
    public ModelRenderer face2;
    public ModelRenderer face3;
    public ModelRenderer nose;
    public ModelRenderer ear1;
    public ModelRenderer ear2;
    public ModelRenderer mouth;
    public ModelRenderer shinRB;
    public ModelRenderer footRB;
    public ModelRenderer claw1rb;
    public ModelRenderer claw2rb;
    public ModelRenderer claw3rb;
    public ModelRenderer claw4rb;
    public ModelRenderer claw5rb;
    public ModelRenderer shinRF;
    public ModelRenderer footRF;
    public ModelRenderer claw1rf;
    public ModelRenderer claw2rf;
    public ModelRenderer claw3rf;
    public ModelRenderer claw4rf;
    public ModelRenderer claw5rf;
    public ModelRenderer shinLF;
    public ModelRenderer footLF;
    public ModelRenderer claw1lf;
    public ModelRenderer claw2lf;
    public ModelRenderer claw3lf;
    public ModelRenderer claw4lf;
    public ModelRenderer claw5lf;

    public ModelBadgerMole() {
        this.textureWidth = 256;
        this.textureHeight = 256;
        this.claw3lf = new ModelRenderer(this, 200, 0);
        this.claw3lf.setRotationPoint(4.5F, 0.0F, 0.0F);
        this.claw3lf.addBox(0.0F, 0.0F, -6.0F, 1, 2, 6, 0.0F);
        this.body2 = new ModelRenderer(this, 0, 107);
        this.body2.setRotationPoint(0.0F, -20.4F, 2.5F);
        this.body2.addBox(-13.0F, 0.0F, 0.0F, 26, 23, 13, 0.0F);
        this.setRotateAngle(body2, -0.15707963267948966F, 0.0F, 0.0F);
        this.body6 = new ModelRenderer(this, 100, 229);
        this.body6.setRotationPoint(0.0F, 11.7F, 10.7F);
        this.body6.addBox(-11.0F, 0.0F, 0.0F, 22, 13, 12, 0.0F);
        this.setRotateAngle(body6, 1.7278759594743864F, 0.0F, 0.0F);
        this.claw4lf = new ModelRenderer(this, 200, 0);
        this.claw4lf.setRotationPoint(6.5F, 0.0F, 0.0F);
        this.claw4lf.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.claw4lb = new ModelRenderer(this, 160, 15);
        this.claw4lb.setRotationPoint(6.5F, 0.0F, 0.0F);
        this.claw4lb.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.face1 = new ModelRenderer(this, 100, 22);
        this.face1.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.face1.addBox(-5.5F, 5.0F, -8.0F, 11, 6, 10, 0.0F);
        this.setRotateAngle(face1, 0.3665191429188092F, 0.0F, 0.0F);
        this.claw3rf = new ModelRenderer(this, 200, 0);
        this.claw3rf.setRotationPoint(4.5F, 0.0F, 0.0F);
        this.claw3rf.addBox(0.0F, 0.0F, -6.0F, 1, 2, 6, 0.0F);
        this.Head = new ModelRenderer(this, 100, 0);
        this.Head.setRotationPoint(0.0F, -10.5F, -25.1F);
        this.Head.addBox(-5.5F, 0.0F, 0.0F, 11, 11, 10, 0.0F);
        this.setRotateAngle(Head, 0.3490658503988659F, 0.0F, 0.0F);
        this.claw1rf = new ModelRenderer(this, 200, 0);
        this.claw1rf.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.claw1rf.addBox(0.0F, 0.0F, -2.0F, 1, 2, 2, 0.0F);
        this.mouth = new ModelRenderer(this, 120, 80);
        this.mouth.setRotationPoint(0.0F, 0.0F, -1.5F);
        this.mouth.addBox(-4.5F, -2.5F, -16.0F, 9, 8, 6, 0.0F);
        this.setRotateAngle(mouth, 0.8726646259971648F, 0.0F, 0.0F);
        this.claw2rf = new ModelRenderer(this, 200, 0);
        this.claw2rf.setRotationPoint(2.5F, 0.0F, 0.0F);
        this.claw2rf.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.footLB = new ModelRenderer(this, 200, 20);
        this.footLB.setRotationPoint(-0.6F, 21.7F, -2.0F);
        this.footLB.addBox(0.5F, 0.0F, 0.0F, 10, 2, 9, 0.0F);
        this.setRotateAngle(footLB, 0.45378560551852565F, 0.0F, 0.0F);
        this.claw1lb = new ModelRenderer(this, 160, 0);
        this.claw1lb.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.claw1lb.addBox(0.0F, 0.0F, -2.0F, 1, 2, 2, 0.0F);
        this.Body = new ModelRenderer(this, 0, 38);
        this.Body.setRotationPoint(-0.5F, -13.2F, -22.0F);
        this.Body.addBox(-12.5F, 0.0F, 0.0F, 26, 14, 13, 0.0F);
        this.setRotateAngle(Body, 0.40142572795869574F, 0.0F, 0.0F);
        this.body5 = new ModelRenderer(this, 100, 170);
        this.body5.setRotationPoint(0.0F, 6.7F, -10.5F);
        this.body5.addBox(-13.0F, 0.0F, 0.0F, 26, 9, 11, 0.0F);
        this.setRotateAngle(body5, 1.2217304763960306F, 0.0F, 0.0F);
        this.ear2 = new ModelRenderer(this, 100, 93);
        this.ear2.setRotationPoint(-6.0F, 4.0F, 1.0F);
        this.ear2.addBox(0.0F, -4.0F, -1.0F, 2, 3, 5, 0.0F);
        this.setRotateAngle(ear2, 0.5585053606381855F, -0.7853981633974483F, 0.0F);
        this.claw1rb = new ModelRenderer(this, 160, 0);
        this.claw1rb.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.claw1rb.addBox(0.0F, 0.0F, -2.0F, 1, 2, 2, 0.0F);
        this.footLF = new ModelRenderer(this, 200, 20);
        this.footLF.setRotationPoint(0.4F, 28.5F, -10.0F);
        this.footLF.addBox(-0.5F, 0.0F, 0.0F, 10, 2, 9, 0.0F);
        this.ear1 = new ModelRenderer(this, 100, 81);
        this.ear1.setRotationPoint(6.0F, 4.0F, 1.0F);
        this.ear1.addBox(-2.0F, -4.0F, -1.0F, 2, 3, 5, 0.0F);
        this.setRotateAngle(ear1, 0.5585053606381855F, 0.7853981633974483F, 0.0F);
        this.claw5lb = new ModelRenderer(this, 160, 0);
        this.claw5lb.setRotationPoint(8.5F, 0.0F, 0.0F);
        this.claw5lb.addBox(0.0F, 0.0F, -2.0F, 1, 2, 2, 0.0F);
        this.face3 = new ModelRenderer(this, 100, 55);
        this.face3.setRotationPoint(0.0F, 0.0F, 1.0F);
        this.face3.addBox(-5.5F, -0.4F, -4.7F, 11, 5, 8, 0.0F);
        this.setRotateAngle(face3, 0.3647738136668149F, 0.0F, 0.0F);
        this.shinLB = new ModelRenderer(this, 0, 0);
        this.shinLB.setRotationPoint(5.0F, 6.8F, -1.5F);
        this.shinLB.addBox(-5.0F, 0.0F, -5.0F, 10, 15, 10, 0.0F);
        this.setRotateAngle(shinLB, 0.45378560551852565F, 0.0F, 0.0F);
        this.shinLF = new ModelRenderer(this, 0, 0);
        this.shinLF.setRotationPoint(5.0F, 12.5F, 0.2F);
        this.shinLF.addBox(-5.0F, 0.0F, -5.0F, 10, 18, 12, 0.0F);
        this.setRotateAngle(shinLF, -0.2617993877991494F, 0.0F, 0.0F);
        this.claw5lf = new ModelRenderer(this, 200, 0);
        this.claw5lf.setRotationPoint(8.5F, 0.0F, 0.0F);
        this.claw5lf.addBox(0.0F, 0.0F, -2.0F, 1, 2, 2, 0.0F);
        this.claw1lf = new ModelRenderer(this, 200, 0);
        this.claw1lf.setRotationPoint(0.5F, 0.0F, 0.0F);
        this.claw1lf.addBox(0.0F, 0.0F, -5.0F, 1, 2, 5, 0.0F);
        this.claw3rb = new ModelRenderer(this, 160, 15);
        this.claw3rb.setRotationPoint(4.5F, 0.0F, 0.0F);
        this.claw3rb.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.claw5rf = new ModelRenderer(this, 200, 0);
        this.claw5rf.setRotationPoint(8.5F, 0.0F, 0.0F);
        this.claw5rf.addBox(0.0F, 0.0F, -5.0F, 1, 2, 5, 0.0F);
        this.tail1 = new ModelRenderer(this, 200, 40);
        this.tail1.setRotationPoint(0.0F, 5.0F, 34.0F);
        this.tail1.addBox(-3.0F, -3.5F, -3.0F, 6, 6, 18, 0.0F);
        this.setRotateAngle(tail1, -0.7853981633974483F, 0.0F, 0.0F);
        this.claw4rb = new ModelRenderer(this, 160, 15);
        this.claw4rb.setRotationPoint(6.5F, 0.0F, 0.0F);
        this.claw4rb.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.body1 = new ModelRenderer(this, 0, 67);
        this.body1.setRotationPoint(0.0F, -18.2F, -10.0F);
        this.body1.addBox(-13.0F, 0.0F, 0.0F, 26, 23, 13, 0.0F);
        this.setRotateAngle(body1, 0.17453292519943295F, 0.0F, 0.0F);
        this.LegLF = new ModelRenderer(this, 0, 0);
        this.LegLF.setRotationPoint(8.0F, -7.0F, -10.0F);
        this.LegLF.addBox(0.0F, -1.0F, -5.0F, 10, 16, 12, 0.0F);
        this.claw2rb = new ModelRenderer(this, 160, 15);
        this.claw2rb.setRotationPoint(2.5F, 0.0F, 0.0F);
        this.claw2rb.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.footRB = new ModelRenderer(this, 200, 20);
        this.footRB.setRotationPoint(-10.4F, 21.7F, -2.0F);
        this.footRB.addBox(0.5F, 0.0F, 0.0F, 10, 2, 9, 0.0F);
        this.setRotateAngle(footRB, 0.45378560551852565F, 0.0F, 0.0F);
        this.LegRF = new ModelRenderer(this, 0, 0);
        this.LegRF.setRotationPoint(-8.0F, -7.0F, -10.0F);
        this.LegRF.addBox(-10.0F, -1.0F, -5.0F, 10, 16, 14, 0.0F);
        this.body2_2 = new ModelRenderer(this, 0, 187);
        this.body2_2.setRotationPoint(0.0F, -13.4F, 26.5F);
        this.body2_2.addBox(-9.0F, -0.5F, 0.7F, 18, 18, 18, 0.0F);
        this.setRotateAngle(body2_2, -1.064650843716541F, 0.0F, 0.0F);
        this.shinRB = new ModelRenderer(this, 0, 0);
        this.shinRB.setRotationPoint(0.0F, 6.8F, -1.5F);
        this.shinRB.addBox(-10.0F, 0.0F, -5.0F, 10, 15, 10, 0.0F);
        this.setRotateAngle(shinRB, 0.45378560551852565F, 0.0F, 0.0F);
        this.claw2lb = new ModelRenderer(this, 160, 15);
        this.claw2lb.setRotationPoint(2.5F, 0.0F, 0.0F);
        this.claw2lb.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.body2_1 = new ModelRenderer(this, 0, 145);
        this.body2_1.setRotationPoint(0.0F, -18.4F, 15.5F);
        this.body2_1.addBox(-11.0F, 0.0F, 0.0F, 22, 23, 13, 0.0F);
        this.setRotateAngle(body2_1, -0.4363323129985824F, 0.0F, 0.0F);
        this.nose = new ModelRenderer(this, 100, 71);
        this.nose.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.nose.addBox(-2.0F, 8.4F, -13.0F, 4, 3, 3, 0.0F);
        this.setRotateAngle(nose, 0.12217304763960307F, 0.0F, 0.0F);
        this.LegLB = new ModelRenderer(this, 0, 0);
        this.LegLB.setRotationPoint(7.0F, 3.0F, 23.0F);
        this.LegLB.addBox(0.0F, -5.0F, -6.0F, 10, 14, 11, 0.0F);
        this.setRotateAngle(LegLB, -0.45378560551852565F, 0.0F, 0.0F);
        this.footRF = new ModelRenderer(this, 200, 20);
        this.footRF.setRotationPoint(-10.4F, 28.5F, -10.0F);
        this.footRF.addBox(0.5F, 0.0F, 0.0F, 10, 2, 9, 0.0F);
        this.tail2 = new ModelRenderer(this, 200, 40);
        this.tail2.setRotationPoint(0.0F, 2.0F, 15.0F);
        this.tail2.addBox(-2.0F, -5.5F, 0.0F, 4, 6, 18, 0.0F);
        this.setRotateAngle(tail2, 0.3490658503988659F, 0.0F, 0.0F);
        this.claw3lb = new ModelRenderer(this, 160, 15);
        this.claw3lb.setRotationPoint(4.5F, 0.0F, 0.0F);
        this.claw3lb.addBox(0.0F, 0.0F, -4.0F, 1, 2, 4, 0.0F);
        this.claw4rf = new ModelRenderer(this, 200, 0);
        this.claw4rf.setRotationPoint(6.5F, 0.0F, 0.0F);
        this.claw4rf.addBox(0.0F, 0.0F, -7.0F, 1, 2, 7, 0.0F);
        this.body6_1 = new ModelRenderer(this, 100, 140);
        this.body6_1.setRotationPoint(0.0F, 9.7F, -2.3F);
        this.body6_1.addBox(-13.0F, 0.0F, 0.0F, 26, 13, 11, 0.0F);
        this.setRotateAngle(body6_1, 1.4311699866353502F, 0.0F, 0.0F);
        this.LegRB = new ModelRenderer(this, 0, 0);
        this.LegRB.setRotationPoint(-7.0F, 3.0F, 23.0F);
        this.LegRB.addBox(-10.0F, -5.0F, -6.0F, 10, 14, 11, 0.0F);
        this.setRotateAngle(LegRB, -0.45378560551852565F, 0.0F, 0.0F);
        this.claw2lf = new ModelRenderer(this, 200, 0);
        this.claw2lf.setRotationPoint(2.5F, 0.0F, 0.0F);
        this.claw2lf.addBox(0.0F, 0.0F, -7.0F, 1, 2, 7, 0.0F);
        this.claw5rb = new ModelRenderer(this, 160, 0);
        this.claw5rb.setRotationPoint(8.5F, 0.0F, 0.0F);
        this.claw5rb.addBox(0.0F, 0.0F, -2.0F, 1, 2, 2, 0.0F);
        this.face2 = new ModelRenderer(this, 100, 40);
        this.face2.setRotationPoint(0.0F, 0.0F, -2.0F);
        this.face2.addBox(-5.5F, 0.0F, -10.0F, 11, 5, 8, 0.0F);
        this.setRotateAngle(face2, 0.6632251157578453F, 0.0F, 0.0F);
        this.shinRF = new ModelRenderer(this, 0, 0);
        this.shinRF.setRotationPoint(0.0F, 12.5F, 0.2F);
        this.shinRF.addBox(-10.0F, 0.0F, -5.0F, 10, 18, 12, 0.0F);
        this.setRotateAngle(shinRF, -0.2617993877991494F, 0.0F, 0.0F);
        this.body4 = new ModelRenderer(this, 100, 200);
        this.body4.setRotationPoint(0.0F, -0.3F, -16.5F);
        this.body4.addBox(-12.0F, 0.0F, 0.0F, 24, 9, 11, 0.0F);
        this.setRotateAngle(body4, 0.6981317007977318F, 0.0F, 0.0F);
        this.footLF.addChild(this.claw3lf);
        this.footLF.addChild(this.claw4lf);
        this.footLB.addChild(this.claw4lb);
        this.Head.addChild(this.face1);
        this.footRF.addChild(this.claw3rf);
        this.footRF.addChild(this.claw1rf);
        this.Head.addChild(this.mouth);
        this.footRF.addChild(this.claw2rf);
        this.LegLB.addChild(this.footLB);
        this.footLB.addChild(this.claw1lb);
        this.Head.addChild(this.ear2);
        this.footRB.addChild(this.claw1rb);
        this.LegLF.addChild(this.footLF);
        this.Head.addChild(this.ear1);
        this.footLB.addChild(this.claw5lb);
        this.Head.addChild(this.face3);
        this.LegLB.addChild(this.shinLB);
        this.LegLF.addChild(this.shinLF);
        this.footLF.addChild(this.claw5lf);
        this.footLF.addChild(this.claw1lf);
        this.footRB.addChild(this.claw3rb);
        this.footRF.addChild(this.claw5rf);
        this.footRB.addChild(this.claw4rb);
        this.footRB.addChild(this.claw2rb);
        this.LegRB.addChild(this.footRB);
        this.LegRB.addChild(this.shinRB);
        this.footLB.addChild(this.claw2lb);
        this.Head.addChild(this.nose);
        this.LegRF.addChild(this.footRF);
        this.tail1.addChild(this.tail2);
        this.footLB.addChild(this.claw3lb);
        this.footRF.addChild(this.claw4rf);
        this.footLF.addChild(this.claw2lf);
        this.footRB.addChild(this.claw5rb);
        this.Head.addChild(this.face2);
        this.LegRF.addChild(this.shinRF);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

        EntityBadgerMole badgermole = (EntityBadgerMole) entity;
        if (badgermole.isChild()) { // Half the original model size for all baby penguins + translation to keep them flush with the ground
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            GlStateManager.translate(0, 1.48f, 0);
            this.Body.render(scale);
            this.body2.render(scale);
            this.body6.render(scale);
            this.Head.render(scale);
            this.body5.render(scale);
            this.tail1.render(scale);
            this.body1.render(scale);
            this.LegLF.render(scale);
            this.LegRF.render(scale);
            this.body2_2.render(scale);
            this.body2_1.render(scale);
            this.LegLB.render(scale);
            this.body6_1.render(scale);
            this.LegRB.render(scale);
            this.body4.render(scale);
            GlStateManager.popMatrix();
        } else {
            this.Body.render(scale);
            this.body2.render(scale);
            this.body6.render(scale);
            this.Head.render(scale);
            this.body5.render(scale);
            this.tail1.render(scale);
            this.body1.render(scale);
            this.LegLF.render(scale);
            this.LegRF.render(scale);
            this.body2_2.render(scale);
            this.body2_1.render(scale);
            this.LegLB.render(scale);
            this.body6_1.render(scale);
            this.LegRB.render(scale);
            this.body4.render(scale);
        }

    }


    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    //public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
  //                                float headPitch, float scaleFactor, Entity entity) {

    //    this.Head().rotateAngleY = (float) (toRadians(netHeadYaw));
  //      this.Head().rotateAngleX = (float) (toRadians(headPitch));

//        this.LegRF().rotateAngleX = LegLB().rotateAngleX = (float) (cos(limbSwing * 0.36667f) * 1.4 * limbSwingAmount * 0.3);

        //this.LegLF().rotateAngleX = LegRB().rotateAngleX = (float) (cos(limbSwing * 0.36667f + Math.PI) * 1.4 * limbSwingAmount * 0.3);

 //   }

}
