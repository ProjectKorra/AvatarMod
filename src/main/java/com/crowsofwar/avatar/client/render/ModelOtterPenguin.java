package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Otter pengin model
 * 
 * @author talhanation unless otherwise indicated
 */
public class ModelOtterPenguin extends ModelBase {
	// fields
	ModelRenderer beard_2;
	ModelRenderer tail_2;
	ModelRenderer left_shin;
	ModelRenderer left_leg;
	ModelRenderer left_foot;
	ModelRenderer right_shin;
	ModelRenderer right_leg;
	ModelRenderer right_foot;
	ModelRenderer left_arm_2;
	ModelRenderer tail_1;
	ModelRenderer body;
	ModelRenderer left_arm_1;
	ModelRenderer right_arm_2;
	ModelRenderer right_arm_1;
	ModelRenderer head;
	ModelRenderer nose;
	ModelRenderer beard_1;
	
	public ModelOtterPenguin() {
		textureWidth = 128;
		textureHeight = 64;
		
		beard_2 = new ModelRenderer(this, 120, 4);
		beard_2.addBox(-2F, -1.6F, -3.6F, 1, 3, 0);
		beard_2.setRotationPoint(0F, 7F, 0F);
		beard_2.setTextureSize(128, 64);
		beard_2.mirror = true;
		setRotation(beard_2, 0F, 0F, 0F);
		tail_2 = new ModelRenderer(this, 40, 34);
		tail_2.addBox(-2.5F, 3F, -2.8F, 5, 6, 1);
		tail_2.setRotationPoint(0F, 18F, 4F);
		tail_2.setTextureSize(128, 64);
		tail_2.mirror = true;
		setRotation(tail_2, 1.099557F, 0F, 0F);
		left_shin = new ModelRenderer(this, 58, 16);
		left_shin.addBox(-1F, 2F, -1F, 2, 2, 2);
		left_shin.setRotationPoint(2F, 19F, 0F);
		left_shin.setTextureSize(128, 64);
		left_shin.mirror = true;
		setRotation(left_shin, 0F, 0F, 0F);
		left_leg = new ModelRenderer(this, 0, 16);
		left_leg.addBox(-2F, 0F, -2F, 4, 2, 4);
		left_leg.setRotationPoint(2F, 19F, 0F);
		left_leg.setTextureSize(128, 64);
		left_leg.mirror = true;
		setRotation(left_leg, 0F, 0F, 0F);
		left_foot = new ModelRenderer(this, 43, 16);
		left_foot.addBox(-1.5F, 4F, -3F, 3, 1, 4);
		left_foot.setRotationPoint(2F, 19F, 0F);
		left_foot.setTextureSize(128, 64);
		left_foot.mirror = true;
		setRotation(left_foot, 0F, 0F, 0F);
		right_shin = new ModelRenderer(this, 58, 16);
		right_shin.addBox(-1F, 2F, -1F, 2, 2, 2);
		right_shin.setRotationPoint(-2F, 19F, 0F);
		right_shin.setTextureSize(128, 64);
		right_shin.mirror = true;
		setRotation(right_shin, 0F, 0F, 0F);
		right_leg = new ModelRenderer(this, 0, 16);
		right_leg.addBox(-2F, 0F, -2F, 4, 2, 4);
		right_leg.setRotationPoint(-2F, 19F, 0F);
		right_leg.setTextureSize(128, 64);
		right_leg.mirror = true;
		setRotation(right_leg, 0F, 0F, 0F);
		right_foot = new ModelRenderer(this, 43, 16);
		right_foot.addBox(-1.5F, 4F, -3F, 3, 1, 4);
		right_foot.setRotationPoint(-2F, 19F, 0F);
		right_foot.setTextureSize(128, 64);
		right_foot.mirror = true;
		setRotation(right_foot, 0F, 0F, 0F);
		left_arm_2 = new ModelRenderer(this, 85, 0);
		left_arm_2.addBox(1F, 0F, -3F, 1, 10, 4);
		left_arm_2.setRotationPoint(3F, 11F, 1F);
		left_arm_2.setTextureSize(128, 64);
		left_arm_2.mirror = true;
		setRotation(left_arm_2, 0F, 0F, -0.0349066F);
		tail_1 = new ModelRenderer(this, 54, 33);
		tail_1.addBox(-3F, -3F, -1F, 6, 7, 2);
		tail_1.setRotationPoint(0F, 18F, 4F);
		tail_1.setTextureSize(128, 64);
		tail_1.mirror = true;
		setRotation(tail_1, 0.3839724F, 0F, 0F);
		body = new ModelRenderer(this, 1, 29);
		body.addBox(-4F, 0F, -3F, 8, 12, 7);
		body.setRotationPoint(0F, 7F, 0F);
		body.setTextureSize(128, 64);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
		left_arm_1 = new ModelRenderer(this, 85, 0);
		left_arm_1.addBox(0.8F, 0F, -3F, 1, 10, 4);
		left_arm_1.setRotationPoint(3F, 7F, 1F);
		left_arm_1.setTextureSize(128, 64);
		left_arm_1.mirror = true;
		setRotation(left_arm_1, 0F, 0F, -0.1570796F);
		right_arm_2 = new ModelRenderer(this, 73, 0);
		right_arm_2.addBox(-2F, 0F, -3F, 1, 10, 4);
		right_arm_2.setRotationPoint(-3F, 11F, 1F);
		right_arm_2.setTextureSize(128, 64);
		right_arm_2.mirror = true;
		setRotation(right_arm_2, 0F, 0F, 0.0349066F);
		right_arm_1 = new ModelRenderer(this, 73, 0);
		right_arm_1.addBox(-1.8F, 0F, -3F, 1, 10, 4);
		right_arm_1.setRotationPoint(-3F, 7F, 1F);
		right_arm_1.setTextureSize(128, 64);
		right_arm_1.mirror = true;
		setRotation(right_arm_1, 0F, 0F, 0.1570796F);
		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4F, -8F, -3.5F, 8, 8, 8);
		head.setRotationPoint(0F, 7F, 0F);
		head.setTextureSize(128, 64);
		head.mirror = true;
		setRotation(head, 0F, 0F, 0F);
		nose = new ModelRenderer(this, 0, 0);
		nose.addBox(-1F, -3F, -4F, 2, 1, 1);
		nose.setRotationPoint(0F, 7F, 0F);
		nose.setTextureSize(128, 64);
		nose.mirror = true;
		setRotation(nose, 0F, 0F, 0F);
		beard_1 = new ModelRenderer(this, 120, 4);
		beard_1.addBox(1F, -1.6F, -3.6F, 1, 3, 0);
		beard_1.setRotationPoint(0F, 7F, 0F);
		beard_1.setTextureSize(128, 64);
		beard_1.mirror = true;
		setRotation(beard_1, 0F, 0F, 0F);
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		beard_2.render(f5);
		tail_2.render(f5);
		left_shin.render(f5);
		left_leg.render(f5);
		left_foot.render(f5);
		right_shin.render(f5);
		right_leg.render(f5);
		right_foot.render(f5);
		left_arm_2.render(f5);
		tail_1.render(f5);
		body.render(f5);
		left_arm_1.render(f5);
		right_arm_2.render(f5);
		right_arm_1.render(f5);
		head.render(f5);
		nose.render(f5);
		beard_1.render(f5);
	}
	
	// this method by CrowsOfWar
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
}
