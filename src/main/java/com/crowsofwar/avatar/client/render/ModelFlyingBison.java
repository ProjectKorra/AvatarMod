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

import com.crowsofwar.avatar.entity.mob.EntitySkyBison;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * FlyingBison - Captn_Dubz & Mnesikos Created using Tabula 5.1.0
 * <p>
 * <p>
 * Note: This model + texture were edited by Mnesikos, heavily based on original model + texture by Captn_Dubz.
 * <p>
 * (Note: Fields originally were pascal case but modified to camel case by
 * CrowsOfWar)
 *
 * @author Mnesikos (unless otherwise specified)
 */
public class ModelFlyingBison extends ModelBase {

	public ModelBisonSaddle saddle;

	public ModelRenderer leg2;
	public ModelRenderer leg4;
	public ModelRenderer body;
	public ModelRenderer upTail;
	public ModelRenderer leg1;
	public ModelRenderer leg5;
	public ModelRenderer leg6;
	public ModelRenderer leg3;
	public ModelRenderer head;
	public ModelRenderer lowTail;
	public ModelRenderer hair;
	public ModelRenderer ear1;
	public ModelRenderer ear2;
	public ModelRenderer horn1;
	public ModelRenderer horn2;
	public ModelRenderer nose;
	public ModelRenderer cheeks;
	public ModelRenderer jaw;

	private int state = 1; // This is a little helper to check when the bison is on the ground + sitting in order to adjust the saddle

	public ModelFlyingBison() {
		this.textureWidth = 112;
		this.textureHeight = 96;
		this.leg1 = new ModelRenderer(this, 0, 0);
		this.leg1.setRotationPoint(5.98F, 11.98F, -7.98F);
		this.leg1.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.cheeks = new ModelRenderer(this, 0, 80);
		this.cheeks.setRotationPoint(0.0F, 4.5F, -9.99F);
		this.cheeks.addBox(-5.5F, 0.0F, 0.0F, 11, 1, 3, 0.0F);
		this.leg2 = new ModelRenderer(this, 0, 0);
		this.leg2.setRotationPoint(5.98F, 11.98F, 3.0F);
		this.leg2.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.leg4 = new ModelRenderer(this, 0, 0);
		this.leg4.setRotationPoint(-5.98F, 11.98F, -7.98F);
		this.leg4.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.lowTail = new ModelRenderer(this, 32, 72);
		this.lowTail.setRotationPoint(0.0F, 1.52F, 13.5F);
		this.lowTail.addBox(-8.0F, -1.5F, 0.0F, 16, 3, 14, 0.0F);
		this.head = new ModelRenderer(this, 0, 43);
		this.head.setRotationPoint(0.0F, 2.5F, -10.0F);
		this.head.addBox(-5.5F, -6.0F, -10.0F, 11, 11, 10, 0.0F);
		this.upTail = new ModelRenderer(this, 35, 55);
		this.upTail.setRotationPoint(0.0F, -2.8F, 15.0F);
		this.upTail.addBox(-8.5F, 0.0F, 0.0F, 17, 3, 14, 0.0F);
		this.nose = new ModelRenderer(this, 40, 43);
		this.nose.setRotationPoint(0.0F, 2.5F, -10.0F);
		this.nose.addBox(-2.5F, -1.0F, -1.0F, 5, 2, 1, 0.0F);
		this.jaw = new ModelRenderer(this, 42, 43);
		this.jaw.setRotationPoint(0.0F, 4.98F, -4.7F);
		this.jaw.addBox(-5.0F, 0.0F, -5.0F, 10, 2, 10, 0.0F);
		this.body = new ModelRenderer(this, 0, 0);
		this.body.setRotationPoint(0.0F, 4.5F, 3.0F);
		this.body.addBox(-9.0F, -7.5F, -14.0F, 18, 15, 28, 0.0F);
		this.hair = new ModelRenderer(this, 0, 64);
		this.hair.setRotationPoint(0.0F, -3.6F, -5.0F);
		this.hair.addBox(-6.0F, -2.5F, -5.5F, 12, 5, 11, 0.0F);
		this.horn2 = new ModelRenderer(this, 0, 64);
		this.horn2.mirror = true;
		this.horn2.setRotationPoint(-6.5F, -2.0F, -7.0F);
		this.horn2.addBox(-1.0F, -9.0F, -1.0F, 2, 9, 2, 0.0F);
		this.setRotateAngle(horn2, -0.20943951023931953F, 0.0F, -0.03490658503988659F);
		this.leg6 = new ModelRenderer(this, 0, 0);
		this.leg6.setRotationPoint(-5.98F, 11.98F, 13.98F);
		this.leg6.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.ear2 = new ModelRenderer(this, 32, 43);
		this.ear2.mirror = true;
		this.ear2.setRotationPoint(-4.0F, -1.0F, -5.0F);
		this.ear2.addBox(-2.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(ear2, 0.0F, 0.0F, 0.3839724354387525F);
		this.ear1 = new ModelRenderer(this, 32, 43);
		this.ear1.setRotationPoint(4.0F, -1.0F, -5.0F);
		this.ear1.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2, 0.0F);
		this.setRotateAngle(ear1, 0.0F, 0.0F, -0.3839724354387525F);
		this.leg5 = new ModelRenderer(this, 0, 0);
		this.leg5.setRotationPoint(-5.98F, 11.98F, 3.0F);
		this.leg5.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.leg3 = new ModelRenderer(this, 0, 0);
		this.leg3.setRotationPoint(5.98F, 11.98F, 13.98F);
		this.leg3.addBox(-3.0F, 0.0F, -3.0F, 6, 12, 6, 0.0F);
		this.horn1 = new ModelRenderer(this, 0, 64);
		this.horn1.setRotationPoint(6.5F, -2.0F, -7.0F);
		this.horn1.addBox(-1.0F, -9.0F, -1.0F, 2, 9, 2, 0.0F);
		this.setRotateAngle(horn1, -0.20943951023931953F, 0.0F, 0.03490658503988659F);
		this.head.addChild(this.cheeks);
		this.upTail.addChild(this.lowTail);
		this.head.addChild(this.nose);
		this.head.addChild(this.jaw);
		this.head.addChild(this.hair);
		this.head.addChild(this.horn2);
		this.head.addChild(this.ear2);
		this.head.addChild(this.ear1);
		this.head.addChild(this.horn1);

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
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
		float pi = (float) Math.PI;
		EntitySkyBison bison = (EntitySkyBison) entity;
		float degToRad = pi / 180;
		BlockPos below = bison.getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = bison.world.getBlockState(below).getBlock();


		// These set up the head rotations for looking positions
		head.rotateAngleX = headPitch * degToRad + MathHelper.cos(limbSwing * 0.6662f / 3) * 0.1f * limbSwingAmount;
		head.rotateAngleY = netHeadYaw * degToRad;

		if (!bison.isSitting()) {
			if (belowBlock == Blocks.AIR) {
				// This sets each leg's rotation point further up (-1f) than default for the flying animation
				leg1.rotationPointY = leg4.rotationPointY = leg2.rotationPointY = leg5.rotationPointY =
						leg3.rotationPointY = leg6.rotationPointY = 11.98F - 1f;
			} else {
				// This sets each leg's rotation point to the model's default when on the ground for the walking animation
				leg1.rotationPointY = leg4.rotationPointY = leg2.rotationPointY = leg5.rotationPointY =
						leg3.rotationPointY = leg6.rotationPointY = 11.98F;
			}
			// These reset the body, head, + tail positions in case they were adjusted by another animation (eating, sitting, etc)
			// The leg rotations are also reset here for when the bison is not moving

			body.rotationPointY = 4.5f;
			head.rotationPointY = 2.5f;
			upTail.rotationPointY = -2.8f;
			leg1.rotateAngleY = leg4.rotateAngleY = leg2.rotateAngleY = leg5.rotateAngleY =
					leg3.rotateAngleY = leg6.rotateAngleY = 0.0f;


			// These are bases for easy editing of how an animation looks
			float globalSpeed;
			float globalDegree;
			float globalWeight;
			if (belowBlock != Blocks.AIR || bison.isEatingGrass()) {

				// Bases for walking animation
				globalSpeed = 0.5F;
				globalDegree = 0.2F;
				globalWeight = 0F;
			} else {

				// Bases for flying animation
				globalSpeed = 0.14F;
				globalDegree = 0.076F;
				globalWeight = 0.2F;
			}

			// These swing the legs + tail pieces based on the speed, degree, and weight set above
			leg2.rotateAngleX = 1 * limbSwingAmount * globalDegree * MathHelper.cos(limbSwing * globalSpeed) + globalWeight;
			leg4.rotateAngleX = 1 * limbSwingAmount * globalDegree * MathHelper.cos(limbSwing * globalSpeed) + globalWeight;
			leg6.rotateAngleX = 1 * limbSwingAmount * globalDegree * MathHelper.cos(limbSwing * globalSpeed) + globalWeight;

			leg1.rotateAngleX = -1 * limbSwingAmount * globalDegree * MathHelper.cos(limbSwing * globalSpeed) + globalWeight;
			leg3.rotateAngleX = -1 * limbSwingAmount * globalDegree * MathHelper.cos(limbSwing * globalSpeed) + globalWeight;
			leg5.rotateAngleX = -1 * limbSwingAmount * globalDegree * MathHelper.cos(limbSwing * globalSpeed) + globalWeight;

			upTail.rotateAngleX = 1 * limbSwingAmount * 0.4f * MathHelper.cos(limbSwing * 0.2f) - 12 * degToRad;
			lowTail.rotateAngleX = 1 * limbSwingAmount * 0.2f * MathHelper.cos(limbSwing * 0.2f + 0.6f);
		}

		if (bison.isEatingGrass()) {
			float lower = 3;

			// These adjust the body, head, + tail positions to be lower when the bison is eating grass
			body.rotationPointY = lower + 4.5f;
			upTail.rotationPointY = lower - 2.8f;
			head.rotationPointY = 8f;
			// These rotate the head + jaw pieces accordingly
			head.rotateAngleX = 24 * degToRad;
			jaw.rotateAngleX = (MathHelper.cos(bison.getEatGrassTime() / 2f) * 15 + 20) * degToRad;
		} else {
			// This resets the jaw's position
			jaw.rotateAngleX = 0f;
		}
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTickTime);
		float pi = (float) Math.PI;
		EntitySkyBison bison = (EntitySkyBison) entity;
		float degToRad = pi / 180;

		BlockPos below = bison.getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = bison.world.getBlockState(below).getBlock();


		// This sets up the bison flop animation for sitting only when they are grounded
		if (bison.isSitting() && belowBlock != Blocks.AIR) {
			this.state = 2; // Saddle adjustment helper cont.
			float lower = 9;
			// These move the body, head, tail, + leg pieces lower
			body.rotationPointY = lower + 4.5f;
			head.rotationPointY = lower + 2.5f;
			upTail.rotationPointY = lower - 2.8f;
			leg1.rotationPointY = leg4.rotationPointY = leg2.rotationPointY = leg5.rotationPointY =
					leg3.rotationPointY = leg6.rotationPointY = lower + 11.98F;

			// These rotate each of the legs + tail pieces
			leg1.rotateAngleX = leg4.rotateAngleX = leg2.rotateAngleX = leg5.rotateAngleX =
					leg3.rotateAngleX = leg6.rotateAngleX = -90 * degToRad;
			leg1.rotateAngleY = -32 * degToRad;
			leg4.rotateAngleY = 32 * degToRad;
			leg2.rotateAngleY = -90 * degToRad;
			leg5.rotateAngleY = 90 * degToRad;
			leg3.rotateAngleY = -148 * degToRad;
			leg6.rotateAngleY = 148 * degToRad;

			upTail.rotateAngleX = -40 * degToRad;
			lowTail.rotateAngleX = 16 * degToRad;
		} else {

			this.state = 1; // Saddle adjustment helper cont.
		}
	}

	/**
	 * glStateManager calls and 'float scale' lines by CrowsOfWar, all else by
	 * Captn_Dubz + edited by Mnesikos
	 */
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {

		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		EntitySkyBison bison = (EntitySkyBison) entity;
		float size = bison.getCondition().getSizeMultiplier();

		pushMatrix();
		float scale = (float) 2.0 * size;

		// This makes sure the bison stays level with the ground despite any scaling
		translate(0f, 1.5f - 1.5f * scale, -0.1f * scale);
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
			if (this.state == 2) {
				// Adjusts saddle if the bison is sitting + on the ground
				GlStateManager.translate(0f, 0.56f, 0f);
			} else if (bison.isEatingGrass()) {
				// Adjusts saddle if the bison is eating grass
				GlStateManager.translate(0f, 0.186f, 0f);
			}
			// Otherwise the saddle does not need adjusting
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
