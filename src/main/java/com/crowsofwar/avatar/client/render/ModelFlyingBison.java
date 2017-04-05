package com.crowsofwar.avatar.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * FlyingBison - Captn_Dubz Created using Tabula 5.1.0
 * 
 * @author Captn_Dubz
 */
public class ModelFlyingBison extends ModelBase {
	public ModelRenderer Body;
	public ModelRenderer Leg1;
	public ModelRenderer Leg2;
	public ModelRenderer Leg3;
	public ModelRenderer Leg4;
	public ModelRenderer Leg5;
	public ModelRenderer Leg6;
	public ModelRenderer Head;
	public ModelRenderer UpTail;
	public ModelRenderer LowTail;
	public ModelRenderer Hair;
	public ModelRenderer Ear1;
	public ModelRenderer Ear2;
	public ModelRenderer Nose;
	public ModelRenderer Horn1;
	public ModelRenderer Horn2;
	
	public ModelFlyingBison() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.Leg2 = new ModelRenderer(this, 0, 0);
		this.Leg2.setRotationPoint(8.0F, 6.0F, 2.0F);
		this.Leg2.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.LowTail = new ModelRenderer(this, 52, 60);
		this.LowTail.setRotationPoint(1.0F, 3.0F, 22.0F);
		this.LowTail.addBox(-9.5F, -1.5F, 0.0F, 19, 3, 14, 0.0F);
		this.setRotateAngle(LowTail, -0.41887902047863906F, 0.0F, 0.0F);
		this.Leg4 = new ModelRenderer(this, 0, 0);
		this.Leg4.setRotationPoint(-6.0F, 6.0F, -7.0F);
		this.Leg4.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.Body = new ModelRenderer(this, 0, 0);
		this.Body.setRotationPoint(1.0F, 0.0F, 3.0F);
		this.Body.addBox(-10.0F, -6.0F, -13.0F, 20, 12, 24, 0.0F);
		this.Hair = new ModelRenderer(this, 0, 74);
		this.Hair.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Hair.addBox(-6.0F, -6.1F, -11.5F, 12, 5, 11, 0.0F);
		this.UpTail = new ModelRenderer(this, 0, 57);
		this.UpTail.setRotationPoint(1.0F, -4.4F, 12.0F);
		this.UpTail.addBox(-9.5F, -2.0F, 0.0F, 19, 3, 14, 0.0F);
		this.setRotateAngle(UpTail, -0.6632251157578453F, 0.0F, 0.0F);
		this.Leg1 = new ModelRenderer(this, 0, 0);
		this.Leg1.setRotationPoint(8.0F, 6.0F, -7.0F);
		this.Leg1.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.Leg5 = new ModelRenderer(this, 0, 0);
		this.Leg5.setRotationPoint(-6.0F, 6.0F, 2.0F);
		this.Leg5.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.Ear1 = new ModelRenderer(this, 82, 0);
		this.Ear1.setRotationPoint(0.0F, 0.0F, -4.0F);
		this.Ear1.addBox(4.0F, 1.0F, -2.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(Ear1, 0.0F, 0.0F, -0.3839724354387525F);
		this.Horn1 = new ModelRenderer(this, 114, 3);
		this.Horn1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Horn1.addBox(5.5F, -9.0F, -9.0F, 2, 9, 2, 0.0F);
		this.setRotateAngle(Horn1, -0.20943951023931953F, 0.0F, 0.03490658503988659F);
		this.Leg6 = new ModelRenderer(this, 0, 0);
		this.Leg6.setRotationPoint(-6.0F, 6.0F, 11.0F);
		this.Leg6.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.Nose = new ModelRenderer(this, 114, 0);
		this.Nose.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Nose.addBox(-2.0F, 1.5F, -11.5F, 4, 2, 1, 0.0F);
		this.Leg3 = new ModelRenderer(this, 0, 0);
		this.Leg3.setRotationPoint(8.0F, 6.0F, 11.0F);
		this.Leg3.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.Ear2 = new ModelRenderer(this, 106, 0);
		this.Ear2.setRotationPoint(0.0F, 0.0F, -4.0F);
		this.Ear2.addBox(-6.0F, 1.0F, -2.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(Ear2, 0.0F, 0.0F, 0.3839724354387525F);
		this.Horn2 = new ModelRenderer(this, 112, 14);
		this.Horn2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.Horn2.addBox(-7.5F, -9.0F, -9.0F, 2, 9, 2, 0.0F);
		this.setRotateAngle(Horn2, -0.20943951023931953F, 0.0F, -0.03490658503988659F);
		this.Head = new ModelRenderer(this, 48, 36);
		this.Head.setRotationPoint(1.0F, -2.0F, -8.0F);
		this.Head.addBox(-5.5F, -6.0F, -11.0F, 11, 11, 10, 0.0F);
		this.Head.addChild(this.Hair);
		this.Head.addChild(this.Ear1);
		this.Head.addChild(this.Horn1);
		this.Head.addChild(this.Nose);
		this.Head.addChild(this.Ear2);
		this.Head.addChild(this.Horn2);
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.Leg2.render(f5);
		this.LowTail.render(f5);
		this.Leg4.render(f5);
		this.Body.render(f5);
		this.UpTail.render(f5);
		this.Leg1.render(f5);
		this.Leg5.render(f5);
		this.Leg6.render(f5);
		this.Leg3.render(f5);
		this.Head.render(f5);
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
