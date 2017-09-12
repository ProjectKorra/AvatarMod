package com.crowsofwar.avatar.client.render.iceprison;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Ice prison model, melting stage 1 (totally frozen). Model created bu talhanation
 * <p>
 * Ice Prison lvl1 - talhanation
 * Created using Tabula 5.1.0
 */
public class ModelIcePrison1 extends ModelBase {

	private ModelRenderer shape1;
	private ModelRenderer shape2;
	private ModelRenderer shape3;
	private ModelRenderer shape4;
	private ModelRenderer shape5;
	private ModelRenderer shape6;
	private ModelRenderer shape7;
	private ModelRenderer shape8;
	private ModelRenderer shape9;
	private ModelRenderer shape10;
	private ModelRenderer shape11;
	private ModelRenderer shape12;
	private ModelRenderer shape13;
	private ModelRenderer shape14;
	private ModelRenderer shape15;
	private ModelRenderer shape16;
	private ModelRenderer shape17;
	private ModelRenderer shape18;
	private ModelRenderer shape19;
	private ModelRenderer shape20;
	private ModelRenderer shape21;
	private ModelRenderer shape22;
	private ModelRenderer shape23;
	private ModelRenderer shape24;
	private ModelRenderer shape25;

	public ModelIcePrison1() {
		this.textureWidth = 256;
		this.textureHeight = 128;
		this.shape20 = new ModelRenderer(this, 4, 0);
		this.shape20.setRotationPoint(-4.2F, 22.2F, -3.0F);
		this.shape20.addBox(-3.5F, 1.1F, 0.0F, 8, 2, 5, 0.0F);
		this.setRotateAngle(shape20, 0.03490658503988659F, -0.03316125578789226F, -0.03822271061867581F);
		this.shape10 = new ModelRenderer(this, 4, 0);
		this.shape10.setRotationPoint(-0.2F, 22.0F, -3.0F);
		this.shape10.addBox(-7.0F, 0.9F, -9.0F, 7, 2, 5, 0.0F);
		this.setRotateAngle(shape10, 0.03490658503988659F, -0.004363323129985824F, -0.040317105721069016F);
		this.shape21 = new ModelRenderer(this, 9, 0);
		this.shape21.setRotationPoint(0.0F, 19.6F, -3.3F);
		this.shape21.addBox(0.0F, -3.7F, 0.0F, 4, 8, 1, 0.0F);
		this.setRotateAngle(shape21, -0.1832595714594046F, 0.0F, 0.0F);
		this.shape4 = new ModelRenderer(this, 0, 0);
		this.shape4.setRotationPoint(-7.3F, 21.7F, -8.1F);
		this.shape4.addBox(0.0F, 2.0F, 0.0F, 7, 2, 5, 0.0F);
		this.setRotateAngle(shape4, 0.11082840750163991F, -0.05305800926062762F, -0.05323254218582704F);
		this.shape2 = new ModelRenderer(this, 21, 0);
		this.shape2.setRotationPoint(-6.1F, 19.4F, 1.5F);
		this.shape2.addBox(-0.1F, -7.4F, 0.2F, 4, 12, 1, 0.0F);
		this.setRotateAngle(shape2, -0.1589994948566834F, 1.4044664490798369F, 0.016929693744344994F);
		this.shape5 = new ModelRenderer(this, 7, 0);
		this.shape5.setRotationPoint(-0.2F, 22.0F, -3.0F);
		this.shape5.addBox(0.6F, 1.1F, 5.3F, 7, 2, 5, 0.0F);
		this.setRotateAngle(shape5, 0.03490658503988659F, -0.03316125578789226F, 0.08429940287132612F);
		this.shape15 = new ModelRenderer(this, 0, 0);
		this.shape15.setRotationPoint(-3.0F, 19.4F, -3.6F);
		this.shape15.addBox(-0.1F, 0.3F, 0.0F, 4, 6, 1, 0.0F);
		this.setRotateAngle(shape15, -0.8833111344343302F, 0.1741838593490341F, -0.019198621771937624F);
		this.shape23 = new ModelRenderer(this, 9, 0);
		this.shape23.setRotationPoint(0.1F, 19.6F, 2.6F);
		this.shape23.addBox(-5.1F, -6.0F, 0.4F, 3, 10, 2, 0.0F);
		this.setRotateAngle(shape23, 0.20559978588493202F, -0.4553564018453205F, -0.10437068926926091F);
		this.shape7 = new ModelRenderer(this, 8, 15);
		this.shape7.setRotationPoint(-0.2F, 22.0F, -8.1F);
		this.shape7.addBox(0.0F, 1.2F, 0.0F, 8, 2, 5, 0.0F);
		this.setRotateAngle(shape7, 0.03490658503988659F, -0.03316125578789226F, 0.03839724354387525F);
		this.shape18 = new ModelRenderer(this, 2, 0);
		this.shape18.setRotationPoint(-3.0F, 19.4F, -3.6F);
		this.shape18.addBox(2.9F, 0.3F, 0.0F, 4, 6, 1, 0.0F);
		this.setRotateAngle(shape18, -0.8833111344343302F, -0.21380283336930533F, -0.019198621771937624F);
		this.shape19 = new ModelRenderer(this, 4, 0);
		this.shape19.setRotationPoint(-0.2F, 22.0F, -3.0F);
		this.shape19.addBox(0.0F, 1.2F, 0.0F, 8, 2, 5, 0.0F);
		this.setRotateAngle(shape19, 0.03490658503988659F, -0.03316125578789226F, 0.08429940287132612F);
		this.shape17 = new ModelRenderer(this, 2, 0);
		this.shape17.setRotationPoint(-3.0F, 19.4F, -3.6F);
		this.shape17.addBox(2.9F, 0.3F, 0.0F, 4, 6, 1, 0.0F);
		this.setRotateAngle(shape17, -0.8833111344343302F, -0.21380283336930533F, -0.019198621771937624F);
		this.shape22 = new ModelRenderer(this, 9, 0);
		this.shape22.setRotationPoint(0.0F, 19.6F, -3.3F);
		this.shape22.addBox(0.0F, -3.7F, 0.0F, 4, 8, 1, 0.0F);
		this.setRotateAngle(shape22, -0.1832595714594046F, 0.0F, 0.0F);
		this.shape14 = new ModelRenderer(this, 0, 0);
		this.shape14.setRotationPoint(-3.0F, 19.4F, -3.6F);
		this.shape14.addBox(-0.1F, 0.3F, 0.0F, 4, 6, 1, 0.0F);
		this.setRotateAngle(shape14, -0.8833111344343302F, 0.1741838593490341F, -0.019198621771937624F);
		this.shape11 = new ModelRenderer(this, 0, 0);
		this.shape11.setRotationPoint(-3.0F, 19.4F, -3.6F);
		this.shape11.addBox(-0.1F, -4.4F, 0.0F, 4, 9, 1, 0.0F);
		this.setRotateAngle(shape11, -0.39409534510031957F, 0.1741838593490341F, 0.0F);
		this.shape6 = new ModelRenderer(this, 2, 0);
		this.shape6.setRotationPoint(-2.4F, 19.4F, -3.6F);
		this.shape6.addBox(6.2F, -2.0F, 0.0F, 4, 8, 1, 0.0F);
		this.setRotateAngle(shape6, -1.1641346110802178F, -0.091106186954104F, 0.20821777976292352F);
		this.shape13 = new ModelRenderer(this, 8, 0);
		this.shape13.setRotationPoint(-0.2F, 22.0F, -3.0F);
		this.shape13.addBox(0.0F, 0.9F, -9.0F, 8, 2, 5, 0.0F);
		this.setRotateAngle(shape13, 0.03490658503988659F, -0.03316125578789226F, 0.08429940287132612F);
		this.shape9 = new ModelRenderer(this, 21, 0);
		this.shape9.setRotationPoint(-4.6F, 21.3F, -4.4F);
		this.shape9.addBox(-2.1F, -2.3F, -0.6F, 4, 7, 1, 0.0F);
		this.setRotateAngle(shape9, -1.092925177598849F, 0.5618214862169746F, -0.11780972450961724F);
		this.shape8 = new ModelRenderer(this, 4, 0);
		this.shape8.setRotationPoint(-4.2F, 22.2F, -3.0F);
		this.shape8.addBox(-3.5F, 1.1F, 5.0F, 8, 2, 5, 0.0F);
		this.setRotateAngle(shape8, 0.03490658503988659F, -0.03316125578789226F, -0.03822271061867581F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(3.2F, 19.0F, -2.7F);
		this.shape1.addBox(0.0F, -6.0F, -0.9F, 4, 11, 2, 0.0F);
		this.setRotateAngle(shape1, -0.10978120995044333F, -0.7948229413582176F, 0.0F);
		this.shape25 = new ModelRenderer(this, 9, 0);
		this.shape25.setRotationPoint(0.1F, 19.6F, 2.6F);
		this.shape25.addBox(-1.1F, -3.9F, -0.2F, 4, 8, 1, 0.0F);
		this.setRotateAngle(shape25, 0.18203784098300857F, 0.0F, 0.0F);
		this.shape3 = new ModelRenderer(this, 9, 0);
		this.shape3.setRotationPoint(0.1F, 19.6F, 2.6F);
		this.shape3.addBox(-3.1F, -6.1F, 0.2F, 3, 10, 1, 0.0F);
		this.setRotateAngle(shape3, 0.18203784098300857F, -0.20839231268812292F, 0.0F);
		this.shape24 = new ModelRenderer(this, 0, 0);
		this.shape24.setRotationPoint(3.2F, 19.3F, -2.3F);
		this.shape24.addBox(0.3F, -6.0F, -3.9F, 4, 11, 2, 0.0F);
		this.setRotateAngle(shape24, -0.10978120995044333F, -2.170491457780148F, 0.0F);
		this.shape16 = new ModelRenderer(this, 2, 0);
		this.shape16.setRotationPoint(-3.0F, 19.4F, -3.6F);
		this.shape16.addBox(2.9F, 0.3F, 0.0F, 4, 6, 1, 0.0F);
		this.setRotateAngle(shape16, -0.8833111344343302F, -0.21380283336930533F, -0.019198621771937624F);
		this.shape12 = new ModelRenderer(this, 21, 0);
		this.shape12.setRotationPoint(-6.1F, 19.4F, -2.1F);
		this.shape12.addBox(-0.1F, -6.3F, 0.0F, 4, 9, 1, 0.0F);
		this.setRotateAngle(shape12, -0.24766222085799533F, 0.5810201079889122F, 0.016929693744344994F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.shape20.render(f5);
		this.shape10.render(f5);
		this.shape21.render(f5);
		this.shape4.render(f5);
		this.shape2.render(f5);
		this.shape5.render(f5);
		this.shape15.render(f5);
		this.shape23.render(f5);
		this.shape7.render(f5);
		this.shape18.render(f5);
		this.shape19.render(f5);
		this.shape17.render(f5);
		this.shape22.render(f5);
		this.shape14.render(f5);
		this.shape11.render(f5);
		this.shape6.render(f5);
		this.shape13.render(f5);
		this.shape9.render(f5);
		this.shape8.render(f5);
		this.shape1.render(f5);
		this.shape25.render(f5);
		this.shape3.render(f5);
		this.shape24.render(f5);
		this.shape16.render(f5);
		this.shape12.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	private void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}
