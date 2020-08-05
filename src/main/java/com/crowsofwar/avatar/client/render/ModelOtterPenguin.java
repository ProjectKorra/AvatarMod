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

import com.crowsofwar.avatar.common.entity.mob.EntityOtterPenguin;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

/**
 * Otter Penguin - talhanation & edited by Mnesikos
 * Created using Tabula 5.1.0
 *
 * @author Mnesikos (unless otherwise specified)
 */
public class ModelOtterPenguin extends ModelBase {
	public ModelRenderer body;
	public ModelRenderer head;
	public ModelRenderer leftArm1;
	public ModelRenderer leftArm2;
	public ModelRenderer rightArm1;
	public ModelRenderer rightArm2;
	public ModelRenderer leftLeg;
	public ModelRenderer rightLeg;
	public ModelRenderer tail1;
	public ModelRenderer beard1;
	public ModelRenderer beard2;
	public ModelRenderer nose;
	public ModelRenderer leftShin;
	public ModelRenderer leftFoot;
	public ModelRenderer rightShin;
	public ModelRenderer rightFoot;
	public ModelRenderer tail2;
	private int state = 1; // This is a little helper to check when the penguin is being ridden, sprinting, or at its default

	public ModelOtterPenguin() {
		this.textureWidth = 64;
		this.textureHeight = 48;
		this.head = new ModelRenderer(this, 0, 0);
		this.head.setRotationPoint(0.0F, 0.01F, 0.5F);
		this.head.addBox(-3.5F, -7.0F, -4.0F, 7, 7, 8, 0.0F);
		this.leftArm2 = new ModelRenderer(this, 33, 7);
		this.leftArm2.mirror = true;
		this.leftArm2.setRotationPoint(3.5F, 3.0F, -1.5F);
		this.leftArm2.addBox(-0.5F, 0.0F, 0.0F, 1, 10, 4, 0.0F);
		this.setRotateAngle(leftArm2, 0.0F, 0.0F, -0.12217304763960307F);
		this.beard1 = new ModelRenderer(this, 0, 2);
		this.beard1.mirror = true;
		this.beard1.setRotationPoint(1.5F, -1.6F, -4.02F);
		this.beard1.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
		this.rightArm2 = new ModelRenderer(this, 33, 7);
		this.rightArm2.setRotationPoint(-3.5F, 3.0F, -1.5F);
		this.rightArm2.addBox(-0.5F, 0.0F, 0.0F, 1, 10, 4, 0.0F);
		this.setRotateAngle(rightArm2, 0.0F, 0.0F, 0.12217304763960307F);
		this.beard2 = new ModelRenderer(this, 0, 2);
		this.beard2.setRotationPoint(-1.5F, -1.6F, -4.02F);
		this.beard2.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 0, 0.0F);
		this.rightFoot = new ModelRenderer(this, 41, 0);
		this.rightFoot.setRotationPoint(0.0F, 2.0F, -1.5F);
		this.rightFoot.addBox(-1.5F, 0.0F, -2.5F, 3, 1, 4, 0.0F);
		this.tail1 = new ModelRenderer(this, 31, 22);
		this.tail1.setRotationPoint(0.0F, 8.0F, 4.0F);
		this.tail1.addBox(-3.0F, 0.0F, -2.0F, 6, 7, 2, 0.0F);
		this.setRotateAngle(tail1, 0.3839724354387525F, 0.0F, 0.0F);
		this.leftShin = new ModelRenderer(this, 37, 0);
		this.leftShin.mirror = true;
		this.leftShin.setRotationPoint(0.0F, 2.0F, 1.0F);
		this.leftShin.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		this.leftFoot = new ModelRenderer(this, 41, 0);
		this.leftFoot.mirror = true;
		this.leftFoot.setRotationPoint(0.0F, 2.0F, -1.5F);
		this.leftFoot.addBox(-1.5F, 0.0F, -2.5F, 3, 1, 4, 0.0F);
		this.leftArm1 = new ModelRenderer(this, 33, 7);
		this.leftArm1.mirror = true;
		this.leftArm1.setRotationPoint(4.3F, 0.0F, -1.5F);
		this.leftArm1.addBox(-0.5F, 0.0F, 0.0F, 1, 10, 4, 0.0F);
		this.setRotateAngle(leftArm1, 0.0F, 0.0F, -0.10471975511965977F);
		this.nose = new ModelRenderer(this, 0, 0);
		this.nose.setRotationPoint(0.0F, -2.5F, -3.6F);
		this.nose.addBox(-1.0F, -0.5F, -1.0F, 2, 1, 1, 0.0F);
		this.rightShin = new ModelRenderer(this, 37, 0);
		this.rightShin.setRotationPoint(0.0F, 2.0F, 1.0F);
		this.rightShin.addBox(-1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F);
		this.leftLeg = new ModelRenderer(this, 25, 0);
		this.leftLeg.mirror = true;
		this.leftLeg.setRotationPoint(2.1F, 11.99F, 0.2F);
		this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F);
		this.tail2 = new ModelRenderer(this, 47, 22);
		this.tail2.setRotationPoint(0.0F, 7.0F, -1.3F);
		this.tail2.addBox(-2.5F, 0.0F, 0.0F, 5, 6, 1, 0.0F);
		this.setRotateAngle(tail2, 0.6981317007977318F, 0.0F, 0.0F);
		this.rightArm1 = new ModelRenderer(this, 33, 7);
		this.rightArm1.setRotationPoint(-4.3F, 0.0F, -1.5F);
		this.rightArm1.addBox(-0.5F, 0.0F, 0.0F, 1, 10, 4, 0.0F);
		this.setRotateAngle(rightArm1, 0.0F, 0.0F, 0.10471975511965977F);
		this.body = new ModelRenderer(this, 0, 17);
		this.body.setRotationPoint(0.0F, 7.0F, 0.0F);
		this.body.addBox(-4.0F, 0.0F, -3.0F, 8, 12, 7, 0.0F);
		this.rightLeg = new ModelRenderer(this, 25, 0);
		this.rightLeg.setRotationPoint(-2.1F, 11.99F, 0.2F);
		this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F);
		this.body.addChild(this.head);
		this.body.addChild(this.leftArm2);
		this.head.addChild(this.beard1);
		this.body.addChild(this.rightArm2);
		this.head.addChild(this.beard2);
		this.rightShin.addChild(this.rightFoot);
		this.body.addChild(this.tail1);
		this.leftLeg.addChild(this.leftShin);
		this.leftShin.addChild(this.leftFoot);
		this.body.addChild(this.leftArm1);
		this.head.addChild(this.nose);
		this.rightLeg.addChild(this.rightShin);
		this.body.addChild(this.leftLeg);
		this.tail1.addChild(this.tail2);
		this.body.addChild(this.rightArm1);
		this.body.addChild(this.rightLeg);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		EntityOtterPenguin penguin = (EntityOtterPenguin) entity;

		if (penguin.isChild()) { // Half the original model size for all baby penguins + translation to keep them flush with the ground
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5f, 0.5f, 0.5f);
			GlStateManager.translate(0, 1.48f, 0);
			this.body.render(scale);
			GlStateManager.popMatrix();
		} else if (penguin.isBeingRidden()) { // Translation to keep this particular animation flush with the ground
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.88f, 0);
			this.body.render(scale);
			GlStateManager.popMatrix();
		} else {
			// With every other part parented to this piece, this is the only thing needing rendered! Otherwise they would be rendered twice.
			this.body.render(scale);
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
								  float headPitch, float scaleFactor, Entity entity) {
		EntityOtterPenguin penguin = (EntityOtterPenguin) entity;

		if (this.state < 3) { // State 1 and 2 are the default and sprinting states respectively
			// These set up the head rotations for looking positions
			head.rotateAngleY = (float) Math.toRadians(netHeadYaw);
			head.rotateAngleX = (float) Math.toRadians(headPitch);

			// These are bases for easy editing of how an animation looks
			float globalSpeed = 2.0F;
			float globalDegree = 1.8F;
			float globalHeight = 1.6F;
			if (penguin.isChild()) { // Setting all child model animation speeds to be half of the adult's
				globalSpeed = 1.0F;
			}

			// This moves the entire model up and down for a bounce effect in their walk
			body.rotationPointY = (float) -Math.abs((Math.sin(limbSwing * (0.5f * globalSpeed)) * limbSwingAmount * (0.4f * globalHeight))) + 7.0f;
			// This moves the entire model side to side for a waddle effect in their walk
			body.rotateAngleZ = 1 * limbSwingAmount * (0.06F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed));
			// These two rotate the entire model around the Y axis, and the head is rotated to offset the rest of the model
			body.rotateAngleY = 1 * limbSwingAmount * (0.08F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed));
			head.rotateAngleY = -1 * limbSwingAmount * (0.08F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed));

			// These rotate the arms to give them a sort of flap when moving (while sprinting they are raised higher to give a frantic look)
			if (this.state == 2) { // If this entity is sprinting
				leftArm1.rotateAngleZ = 1 * limbSwingAmount * (0.2F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) - 0.6F;
				leftArm2.rotateAngleZ = 1 * limbSwingAmount * (0.2F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) - 0.2F;
				rightArm1.rotateAngleZ = -1 * limbSwingAmount * (0.2F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) + 0.6F;
				rightArm2.rotateAngleZ = -1 * limbSwingAmount * (0.2F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) + 0.2F;
			} else {
				leftArm1.rotateAngleZ = 1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (1.0F * globalSpeed)) - 0.12F;
				leftArm2.rotateAngleZ = 1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (1.0F * globalSpeed)) - 0.12F;
				rightArm1.rotateAngleZ = -1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (1.0F * globalSpeed)) + 0.12F;
				rightArm2.rotateAngleZ = -1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (1.0F * globalSpeed)) + 0.12F;
			}

			// These rotate the legs back and forth + the feet to give a "step" look while walking
			rightLeg.rotateAngleX = 1 * limbSwingAmount * (0.3F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed));
			rightFoot.rotateAngleX = 1 * limbSwingAmount * (0.1F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed) + 2.6F);
			leftLeg.rotateAngleX = -1 * limbSwingAmount * (0.3F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed));
			leftFoot.rotateAngleX = -1 * limbSwingAmount * (0.1F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed) + 2.6F);

			// These rotate the tail pieces up and down slightly, giving the overall animation more life
			tail1.rotateAngleX = 1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (1.0F * globalSpeed)) + 0.3839724354387525F;
			tail2.rotateAngleX = -1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (1.0F * globalSpeed)) + 0.6981317007977318F;

		} else if (this.state == 3) { // If this entity is being ridden
			// These set up the head rotations for looking positions, again.
			// This time with an offset for Angle X to make sure they're looking up instead of in the ground
			head.rotateAngleY = (float) Math.toRadians(netHeadYaw);
			head.rotateAngleX = (float) Math.toRadians(headPitch) + -1.4660765716752369F;

			// More bases for easy editing of how the new animation looks
			float globalSpeed = 0.5F;
			float globalDegree = 1.8F;
			// These rotate the arms to give them a very small flap when moving
			leftArm1.rotateAngleZ = 1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) - 0.8377580409572781F;
			leftArm2.rotateAngleZ = 1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) - 0.6981317007977318F;
			rightArm1.rotateAngleZ = -1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) + 0.8377580409572781F;
			rightArm2.rotateAngleZ = -1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) + 0.6981317007977318F;

			// These are set to the model's defaults so another animation will not affect them while in state 3
			body.rotationPointY = 7.0F;
			leftLeg.rotateAngleX = rightLeg.rotateAngleX = 0.0F;

			// These rotate the tail pieces up and down, giving the overall animation more life
			tail1.rotateAngleX = 1 * limbSwingAmount * (0.01F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) + 0.06981317007977318F;
			tail2.rotateAngleX = 1 * limbSwingAmount * (0.02F * globalDegree) * MathHelper.cos(limbSwing * (0.5F * globalSpeed)) + 0.06981317007977318F;
		}
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
		EntityOtterPenguin penguin = (EntityOtterPenguin) entity;
		// These set the model's default, that way they will revert when no animation is changing them
		body.rotateAngleX = head.rotateAngleX = leftFoot.rotateAngleX = rightFoot.rotateAngleX = 0.0F;
		tail1.rotateAngleX = 0.3839724354387525F;
		tail2.rotateAngleX = 0.6981317007977318F;
		leftArm1.rotateAngleY = leftArm2.rotateAngleY = rightArm1.rotateAngleY = rightArm2.rotateAngleY = 0.0F;
		leftArm1.rotateAngleZ = -0.10471975511965977F;
		leftArm2.rotateAngleZ = -0.12217304763960307F;
		rightArm1.rotateAngleZ = 0.10471975511965977F;
		rightArm2.rotateAngleZ = 0.12217304763960307F;

		if (penguin.isBeingRidden()) {
			// These all adjust each piece's rotations for the new riding pose/animation
			this.setRotateAngle(body, 1.53588974175501F, 0.0F, 0.0F);
			this.setRotateAngle(head, -1.4660765716752369F, 0.0F, 0.0F);
			this.setRotateAngle(leftArm1, 0.0F, -0.8377580409572781F, -0.8377580409572781F);
			this.setRotateAngle(leftArm2, 0.0F, -0.9773843811168246F, -0.6981317007977318F);
			this.setRotateAngle(rightArm1, 0.0F, 0.8377580409572781F, 0.8377580409572781F);
			this.setRotateAngle(rightArm2, 0.0F, 0.9773843811168246F, 0.6981317007977318F);
			this.setRotateAngle(leftFoot, 1.2566370614359172F, 0.0F, 0.0F);
			this.setRotateAngle(rightFoot, 1.2566370614359172F, 0.0F, 0.0F);
			this.setRotateAngle(tail1, 0.06981317007977318F, 0.0F, 0.0F);
			this.setRotateAngle(tail2, 0.06981317007977318F, 0.0F, 0.0F);
			this.state = 3; // Little helper cont.

		} else if (penguin.isSprinting()) {
			this.state = 2; // Little helper cont.

		} else {
			this.state = 1; // Little helper cont.
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
}
