package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

import com.crowsofwar.avatar.common.entity.EntityEarthspike;

/**
 * Earthspike Up - talhanation
 * Created using Tabula 5.1.0
 */
public class ModelEarthspikes extends ModelBase {
	public ModelRenderer shape1;
	public ModelRenderer shape4;
	public ModelRenderer shape7;
	public ModelRenderer shape10;
	public ModelRenderer shape13;
	public ModelRenderer shape16;
	public ModelRenderer shape19;
	public ModelRenderer shape22;
	public ModelRenderer shape25;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape5;
	public ModelRenderer shape6;
	public ModelRenderer shape8;
	public ModelRenderer shape9;
	public ModelRenderer shape11;
	public ModelRenderer shape12;
	public ModelRenderer shape14;
	public ModelRenderer shape15;
	public ModelRenderer shape17;
	public ModelRenderer shape18;
	public ModelRenderer shape20;
	public ModelRenderer shape21;
	public ModelRenderer shape23;
	public ModelRenderer shape24;

	public ModelEarthspikes() {
		textureWidth = 256;
		textureHeight = 128;
		shape3 = new ModelRenderer(this, 29, 0);
		shape3.setRotationPoint(0.0F, -8.0F, -0.5F);
		shape3.addBox(-1.1F, -1.0F, 1.4F, 2, 4, 1, 0.0F);
		shape6 = new ModelRenderer(this, 29, 0);
		shape6.setRotationPoint(0.0F, -8.0F, -0.5F);
		shape6.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		shape25 = new ModelRenderer(this, 0, 0);
		shape25.setRotationPoint(0.0F, 12.0F, 0.0F);
		shape25.addBox(-2.0F, 0.0F, -2.0F, 4, 11, 4, 0.0F);
		shape5 = new ModelRenderer(this, 50, 0);
		shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape5.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		shape9 = new ModelRenderer(this, 29, 0);
		shape9.setRotationPoint(0.0F, -8.0F, -0.5F);
		shape9.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		shape2 = new ModelRenderer(this, 50, 0);
		shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape2.addBox(-2.0F, -5.0F, 0.0F, 3, 8, 2, 0.0F);
		shape10 = new ModelRenderer(this, 0, 0);
		shape10.setRotationPoint(0.0F, 15.0F, -4.5F);
		shape10.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		setRotateAngle(shape10, -0.2617993877991494F, 0.0F, 0.0F);
		shape24 = new ModelRenderer(this, 29, 0);
		shape24.setRotationPoint(0.0F, -8.0F, -0.5F);
		shape24.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		shape14 = new ModelRenderer(this, 50, 0);
		shape14.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape14.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		shape7 = new ModelRenderer(this, 0, 0);
		shape7.setRotationPoint(-4.0F, 15.0F, 0.5F);
		shape7.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		setRotateAngle(shape7, 0.2617993877991494F, -1.5707963267948966F, 0.0F);
		shape17 = new ModelRenderer(this, 50, 0);
		shape17.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape17.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		shape21 = new ModelRenderer(this, 29, 0);
		shape21.setRotationPoint(0.0F, -8.0F, -0.5F);
		shape21.addBox(-1.1F, -1.0F, 1.4F, 2, 4, 1, 0.0F);
		shape20 = new ModelRenderer(this, 50, 0);
		shape20.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape20.addBox(-2.0F, -5.0F, 0.0F, 3, 8, 2, 0.0F);
		shape4 = new ModelRenderer(this, 0, 0);
		shape4.setRotationPoint(-2.0F, 15.0F, 2.5F);
		shape4.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		setRotateAngle(shape4, 0.2617993877991494F, -0.7853981633974483F, 0.0F);
		shape11 = new ModelRenderer(this, 50, 0);
		shape11.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape11.addBox(-2.0F, -5.0F, 0.0F, 3, 8, 2, 0.0F);
		shape16 = new ModelRenderer(this, 0, 0);
		shape16.setRotationPoint(3.0F, 15.0F, 2.5F);
		shape16.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		setRotateAngle(shape16, 0.2617993877991494F, 0.7853981633974483F, 0.0F);
		shape13 = new ModelRenderer(this, 0, 0);
		shape13.setRotationPoint(0.0F, 15.0F, 3.5F);
		shape13.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		setRotateAngle(shape13, 0.2617993877991494F, 0.0F, 0.0F);
		shape15 = new ModelRenderer(this, 29, 0);
		shape15.setRotationPoint(0.0F, -8.0F, -0.5F);
		shape15.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		shape22 = new ModelRenderer(this, 0, 0);
		shape22.setRotationPoint(-3.0F, 15.0F, -2.5F);
		shape22.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		setRotateAngle(shape22, 0.2617993877991494F, -2.356194490192345F, 0.0F);
		shape23 = new ModelRenderer(this, 50, 0);
		shape23.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape23.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		shape18 = new ModelRenderer(this, 29, 0);
		shape18.setRotationPoint(0.0F, -8.0F, -0.5F);
		shape18.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		shape8 = new ModelRenderer(this, 50, 0);
		shape8.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape8.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		shape19 = new ModelRenderer(this, 0, 0);
		shape19.setRotationPoint(5.0F, 15.0F, 0.5F);
		shape19.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		setRotateAngle(shape19, -0.2617993877991494F, -1.5707963267948966F, 0.0F);
		shape12 = new ModelRenderer(this, 29, 0);
		shape12.setRotationPoint(0.0F, -8.0F, -0.5F);
		shape12.addBox(-1.1F, -1.0F, 1.4F, 2, 4, 1, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(3.0F, 15.0F, -2.5F);
		shape1.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		setRotateAngle(shape1, -0.2617993877991494F, -0.7853981633974483F, 0.0F);
		shape1.addChild(shape3);
		shape4.addChild(shape6);
		shape4.addChild(shape5);
		shape7.addChild(shape9);
		shape1.addChild(shape2);
		shape22.addChild(shape24);
		shape13.addChild(shape14);
		shape16.addChild(shape17);
		shape19.addChild(shape21);
		shape19.addChild(shape20);
		shape10.addChild(shape11);
		shape13.addChild(shape15);
		shape22.addChild(shape23);
		shape16.addChild(shape18);
		shape7.addChild(shape8);
		shape10.addChild(shape12);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		EntityEarthspike spike = (EntityEarthspike) entity;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -1.5 * spike.getSize(), 0);
		GlStateManager.scale(spike.getSize(), spike.getSize(), spike.getSize());
		shape25.render(f5);
		shape10.render(f5);
		shape7.render(f5);
		shape4.render(f5);
		shape16.render(f5);
		shape13.render(f5);
		shape22.render(f5);
		shape19.render(f5);
		shape1.render(f5);
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
}
