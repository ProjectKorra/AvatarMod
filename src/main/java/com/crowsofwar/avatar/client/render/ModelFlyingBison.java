/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * FlyingBison - Captn_Dubz Created using Tabula 5.1.0
 * <p>
 * (Note: Fields originally were pascal case but modified to camel case by
 * CrowsOfWar)
 *
 * @author Captn_Dubz (unless otherwise specified)
 */
public class ModelFlyingBison extends ModelBase {

	public ModelBisonSaddle saddle;

	public ModelRenderer body;
	public ModelRenderer leg1;
	public ModelRenderer leg2;
	public ModelRenderer leg3;
	public ModelRenderer leg4;
	public ModelRenderer leg5;
	public ModelRenderer leg6;
	public ModelRenderer head;
	public ModelRenderer upTail;
	public ModelRenderer lowTail;
	public ModelRenderer hair;
	public ModelRenderer ear1;
	public ModelRenderer ear2;
	public ModelRenderer nose;
	public ModelRenderer horn1;
	public ModelRenderer horn2;

	public ModelFlyingBison() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.leg2 = new ModelRenderer(this, 0, 0);
		this.leg2.setRotationPoint(8.0F, 6.0F, 2.0F);
		this.leg2.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);

		this.leg4 = new ModelRenderer(this, 0, 0);
		this.leg4.setRotationPoint(-6.0F, 6.0F, -7.0F);
		this.leg4.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.body = new ModelRenderer(this, 0, 0);
		this.body.setRotationPoint(1.0F, 0.0F, 3.0F);
		this.body.addBox(-10.0F, -6.0F, -13.0F, 20, 12, 24, 0.0F);
		this.hair = new ModelRenderer(this, 0, 74);
		this.hair.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.hair.addBox(-6.0F, -6.1F, -11.5F, 12, 5, 11, 0.0F);

		this.upTail = new ModelRenderer(this, 0, 57);
		this.upTail.setRotationPoint(1.0F, -4.4F, 12.0F);
		this.upTail.addBox(-9.5F, -2.0F, 0.0F, 19, 3, 14, 0.0F);
		this.setRotateAngle(upTail, 0, 0.0F, 0.0F);

		this.lowTail = new ModelRenderer(this, 52, 60);
		this.lowTail.setRotationPoint(1.0F, -0.5F, 14.0F);
		this.lowTail.addBox(-9.5F, -1.5F, 0F, 19, 3, 14, 0.0F);
		this.setRotateAngle(lowTail, 0, 0.0F, 0.0F);

		this.upTail.addChild(lowTail);

		this.leg1 = new ModelRenderer(this, 0, 0);
		this.leg1.setRotationPoint(8.0F, 6.0F, -7.0F);
		this.leg1.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.leg5 = new ModelRenderer(this, 0, 0);
		this.leg5.setRotationPoint(-6.0F, 6.0F, 2.0F);
		this.leg5.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.ear1 = new ModelRenderer(this, 82, 0);
		this.ear1.setRotationPoint(0.0F, 0.0F, -4.0F);
		this.ear1.addBox(4.0F, 1.0F, -2.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(ear1, 0.0F, 0.0F, -0.3839724354387525F);
		this.horn1 = new ModelRenderer(this, 114, 3);
		this.horn1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.horn1.addBox(5.5F, -9.0F, -9.0F, 2, 9, 2, 0.0F);
		this.setRotateAngle(horn1, -0.20943951023931953F, 0.0F, 0.03490658503988659F);
		this.leg6 = new ModelRenderer(this, 0, 0);
		this.leg6.setRotationPoint(-6.0F, 6.0F, 11.0F);
		this.leg6.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.nose = new ModelRenderer(this, 114, 0);
		this.nose.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.nose.addBox(-2.0F, 1.5F, -11.5F, 4, 2, 1, 0.0F);
		this.leg3 = new ModelRenderer(this, 0, 0);
		this.leg3.setRotationPoint(8.0F, 6.0F, 11.0F);
		this.leg3.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.ear2 = new ModelRenderer(this, 106, 0);
		this.ear2.setRotationPoint(0.0F, 0.0F, -4.0F);
		this.ear2.addBox(-6.0F, 1.0F, -2.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(ear2, 0.0F, 0.0F, 0.3839724354387525F);
		this.horn2 = new ModelRenderer(this, 112, 14);
		this.horn2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.horn2.addBox(-7.5F, -9.0F, -9.0F, 2, 9, 2, 0.0F);
		this.setRotateAngle(horn2, -0.20943951023931953F, 0.0F, -0.03490658503988659F);
		this.head = new ModelRenderer(this, 48, 36);
		this.head.setRotationPoint(1.0F, -2.0F, -8.0F);
		this.head.addBox(-5.5F, -6.0F, -11.0F, 11, 11, 10, 0.0F);
		this.head.addChild(this.hair);
		this.head.addChild(this.ear1);
		this.head.addChild(this.horn1);
		this.head.addChild(this.nose);
		this.head.addChild(this.ear2);
		this.head.addChild(this.horn2);

		// CrowsOfWar: Adjust bottom-of-feet pos to be at 0, which prevents
		// weird issues while scaling
		List<ModelRenderer> allBoxes = Arrays.asList(body, leg1, leg2, leg3, leg4, leg5, leg6, head, upTail);
		for (ModelRenderer box : allBoxes) {
			box.rotationPointY -= 18;
		}

		this.saddle = new ModelBisonSaddle();

	}

	/**
	 * Please note, head rotations are in degrees
	 *
	 * @author CrowsOfWar
	 */
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
								  float headPitch, float scaleFactor, Entity entity) {

		float pi = (float) Math.PI;
		EntitySkyBison bison = (EntitySkyBison) entity;
		float degToRad = pi / 180;

		head.rotateAngleX = headPitch * degToRad
				+ MathHelper.cos(limbSwing * 0.6662f / 3) * 0.1f * limbSwingAmount;
		head.rotateAngleY = netHeadYaw * degToRad;

		if (bison.isSitting()) {

			float lower = 3;

			body.rotationPointY = lower + 0 - 18;
			head.rotationPointY = lower - 2 - 18;
			upTail.rotationPointY = lower - 4.4f - 18;

			limbSwing = 2.87f;
			limbSwingAmount = 0.3f;

		} else {
			body.rotationPointY = 0 - 18;
			head.rotationPointY = -2 - 18;
			upTail.rotationPointY = -4.4f - 18;
		}

		leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		leg6.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

		leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + pi) * 1.4F * limbSwingAmount;
		leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + pi) * 1.4F * limbSwingAmount;
		leg5.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + pi) * 1.4F * limbSwingAmount;

		upTail.rotateAngleX = (MathHelper.cos(limbSwing * 0.3331f) - 2f) * 0.2f * limbSwingAmount
				- 20 * degToRad;
		lowTail.rotateAngleX = (MathHelper.cos(limbSwing * 0.3331f) - 2f) * -0.25f * limbSwingAmount;

		if (bison.isEatingGrass()) {
			head.rotateAngleX = (MathHelper.cos(bison.getEatGrassTime() / 2f) * 15 + 65) * degToRad;
		}

	}

	/**
	 * glStateManager calls and 'float scale' lines by CrowsOfWar, all else by
	 * Captn_Dubz
	 */
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {

		EntitySkyBison bison = (EntitySkyBison) entity;
		float size = bison.getCondition().getSizeMultiplier();

		pushMatrix();
		float scale = 1.5f * size;
		translate(0, 1.5, 0);
		GlStateManager.scale(scale, scale, scale);

		this.leg2.render(f5);
		this.leg4.render(f5);
		this.body.render(f5);
		this.upTail.render(f5);
		this.leg1.render(f5);
		this.leg5.render(f5);
		this.leg6.render(f5);
		this.leg3.render(f5);
		this.head.render(f5);

		if (bison.getSaddle() != null) {
			pushMatrix();
			translate(0, -1.55, 0.2);

			if (bison.isSitting()) {
				translate(0, 0.20, 0);
			}

			scale(0.5, 0.5, 0.5);
			this.saddle.render(entity, f, f1, f2, f3, f4, f5);
			popMatrix();
		}

		popMatrix();

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
