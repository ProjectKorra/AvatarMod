package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityEarthspike;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

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
		this.textureWidth = 256;
		this.textureHeight = 128;
		this.shape3 = new ModelRenderer(this, 29, 0);
		this.shape3.setRotationPoint(0.0F, -8.0F, -0.5F);
		this.shape3.addBox(-1.1F, -1.0F, 1.4F, 2, 4, 1, 0.0F);
		this.shape6 = new ModelRenderer(this, 29, 0);
		this.shape6.setRotationPoint(0.0F, -8.0F, -0.5F);
		this.shape6.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		this.shape25 = new ModelRenderer(this, 0, 0);
		this.shape25.setRotationPoint(0.0F, 12.0F, 0.0F);
		this.shape25.addBox(-2.0F, 0.0F, -2.0F, 4, 11, 4, 0.0F);
		this.shape5 = new ModelRenderer(this, 50, 0);
		this.shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape5.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		this.shape9 = new ModelRenderer(this, 29, 0);
		this.shape9.setRotationPoint(0.0F, -8.0F, -0.5F);
		this.shape9.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		this.shape2 = new ModelRenderer(this, 50, 0);
		this.shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape2.addBox(-2.0F, -5.0F, 0.0F, 3, 8, 2, 0.0F);
		this.shape10 = new ModelRenderer(this, 0, 0);
		this.shape10.setRotationPoint(0.0F, 15.0F, -4.5F);
		this.shape10.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		this.setRotateAngle(shape10, -0.2617993877991494F, 0.0F, 0.0F);
		this.shape24 = new ModelRenderer(this, 29, 0);
		this.shape24.setRotationPoint(0.0F, -8.0F, -0.5F);
		this.shape24.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		this.shape14 = new ModelRenderer(this, 50, 0);
		this.shape14.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape14.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		this.shape7 = new ModelRenderer(this, 0, 0);
		this.shape7.setRotationPoint(-4.0F, 15.0F, 0.5F);
		this.shape7.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		this.setRotateAngle(shape7, 0.2617993877991494F, -1.5707963267948966F, 0.0F);
		this.shape17 = new ModelRenderer(this, 50, 0);
		this.shape17.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape17.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		this.shape21 = new ModelRenderer(this, 29, 0);
		this.shape21.setRotationPoint(0.0F, -8.0F, -0.5F);
		this.shape21.addBox(-1.1F, -1.0F, 1.4F, 2, 4, 1, 0.0F);
		this.shape20 = new ModelRenderer(this, 50, 0);
		this.shape20.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape20.addBox(-2.0F, -5.0F, 0.0F, 3, 8, 2, 0.0F);
		this.shape4 = new ModelRenderer(this, 0, 0);
		this.shape4.setRotationPoint(-2.0F, 15.0F, 2.5F);
		this.shape4.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		this.setRotateAngle(shape4, 0.2617993877991494F, -0.7853981633974483F, 0.0F);
		this.shape11 = new ModelRenderer(this, 50, 0);
		this.shape11.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape11.addBox(-2.0F, -5.0F, 0.0F, 3, 8, 2, 0.0F);
		this.shape16 = new ModelRenderer(this, 0, 0);
		this.shape16.setRotationPoint(3.0F, 15.0F, 2.5F);
		this.shape16.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		this.setRotateAngle(shape16, 0.2617993877991494F, 0.7853981633974483F, 0.0F);
		this.shape13 = new ModelRenderer(this, 0, 0);
		this.shape13.setRotationPoint(0.0F, 15.0F, 3.5F);
		this.shape13.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		this.setRotateAngle(shape13, 0.2617993877991494F, 0.0F, 0.0F);
		this.shape15 = new ModelRenderer(this, 29, 0);
		this.shape15.setRotationPoint(0.0F, -8.0F, -0.5F);
		this.shape15.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		this.shape22 = new ModelRenderer(this, 0, 0);
		this.shape22.setRotationPoint(-3.0F, 15.0F, -2.5F);
		this.shape22.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		this.setRotateAngle(shape22, 0.2617993877991494F, -2.356194490192345F, 0.0F);
		this.shape23 = new ModelRenderer(this, 50, 0);
		this.shape23.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape23.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		this.shape18 = new ModelRenderer(this, 29, 0);
		this.shape18.setRotationPoint(0.0F, -8.0F, -0.5F);
		this.shape18.addBox(-1.1F, -1.0F, -0.6F, 2, 4, 1, 0.0F);
		this.shape8 = new ModelRenderer(this, 50, 0);
		this.shape8.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape8.addBox(-2.0F, -5.0F, -1.0F, 3, 8, 2, 0.0F);
		this.shape19 = new ModelRenderer(this, 0, 0);
		this.shape19.setRotationPoint(5.0F, 15.0F, 0.5F);
		this.shape19.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		this.setRotateAngle(shape19, -0.2617993877991494F, -1.5707963267948966F, 0.0F);
		this.shape12 = new ModelRenderer(this, 29, 0);
		this.shape12.setRotationPoint(0.0F, -8.0F, -0.5F);
		this.shape12.addBox(-1.1F, -1.0F, 1.4F, 2, 4, 1, 0.0F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(3.0F, 15.0F, -2.5F);
		this.shape1.addBox(-2.0F, 3.0F, -1.0F, 4, 7, 3, 0.0F);
		this.setRotateAngle(shape1, -0.2617993877991494F, -0.7853981633974483F, 0.0F);
		this.shape1.addChild(this.shape3);
		this.shape4.addChild(this.shape6);
		this.shape4.addChild(this.shape5);
		this.shape7.addChild(this.shape9);
		this.shape1.addChild(this.shape2);
		this.shape22.addChild(this.shape24);
		this.shape13.addChild(this.shape14);
		this.shape16.addChild(this.shape17);
		this.shape19.addChild(this.shape21);
		this.shape19.addChild(this.shape20);
		this.shape10.addChild(this.shape11);
		this.shape13.addChild(this.shape15);
		this.shape22.addChild(this.shape23);
		this.shape16.addChild(this.shape18);
		this.shape7.addChild(this.shape8);
		this.shape10.addChild(this.shape12);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		EntityEarthspike spike = (EntityEarthspike) entity;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -1.5 * spike.getSize(), 0);
		GlStateManager.scale(spike.getSize(), spike.getSize(), spike.getSize());
		this.shape25.render(f5);
		this.shape10.render(f5);
		this.shape7.render(f5);
		this.shape4.render(f5);
		this.shape16.render(f5);
		this.shape13.render(f5);
		this.shape22.render(f5);
		this.shape19.render(f5);
		this.shape1.render(f5);
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
