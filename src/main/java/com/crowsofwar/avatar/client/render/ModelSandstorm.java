package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSandstorm - CrowsOfWar
 * Created using Tabula 7.0.0
 */
public class ModelSandstorm extends ModelBase {
	public ModelRenderer mainPart;
	public ModelRenderer topPart;
	public ModelRenderer groundPart;

	public ModelSandstorm() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.groundPart = new ModelRenderer(this, 40, 0);
		this.groundPart.setRotationPoint(0.0F, 16.0F, 0.0F);
		this.groundPart.addBox(-3.0F, 0.0F, -3.0F, 6, 14, 6, 0.0F);
		this.setRotateAngle(groundPart, 0.0F, 0.045553093477052F, 0.0F);
		this.topPart = new ModelRenderer(this, 0, 28);
		this.topPart.setRotationPoint(0.0F, 2.0F, 0.0F);
		this.topPart.addBox(-7.0F, 0.0F, -7.0F, 14, 7, 14, 0.0F);
		this.setRotateAngle(topPart, 0.0F, -0.31869712141416456F, 0.0F);
		this.mainPart = new ModelRenderer(this, 0, 0);
		this.mainPart.setRotationPoint(0.0F, -16.0F, 0.0F);
		this.mainPart.addBox(-5.0F, 0.0F, -5.0F, 10, 18, 10, 0.0F);
		this.setRotateAngle(mainPart, 0.0F, -0.18203784098300857F, 0.0F);
		this.mainPart.addChild(this.groundPart);
		this.mainPart.addChild(this.topPart);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		this.mainPart.render(f5);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float
			netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {

		EntitySandstorm sandstorm = (EntitySandstorm) entityIn;
		double velocityMultiplier = sandstorm.getVelocityMultiplier();
		float strength = sandstorm.getStrength();

		// Time passed since this sandstorm was last rendered
		float timePassed = ageInTicks - sandstorm.getLastRenderAge();

		float animationProgress = sandstorm.getAnimationProgress();

		float fullRotation = (float) Math.PI * 2;

		float topYaw = animationProgress / 60 * fullRotation;
		float middleYaw = animationProgress / 20 * fullRotation;
		float bottomYaw = animationProgress / 15 * fullRotation;

		groundPart.rotateAngleY = bottomYaw - middleYaw;
		groundPart.rotateAngleX = (float) (Math.sin(animationProgress / 20) * Math.toRadians(6));
		mainPart.rotateAngleY = middleYaw;
		mainPart.rotateAngleZ = (float) (Math.sin(animationProgress / 60) * Math.toRadians(4));
		mainPart.rotateAngleX = (float) (Math.cos(animationProgress / 60) * Math.toRadians(4));
		topPart.rotateAngleY = topYaw - middleYaw;

		sandstorm.setAnimationProgress(animationProgress + timePassed * strength * (float) velocityMultiplier);
		sandstorm.setLastRenderAge(ageInTicks);

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
