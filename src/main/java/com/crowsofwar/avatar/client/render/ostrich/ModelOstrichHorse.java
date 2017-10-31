package com.crowsofwar.avatar.client.render.ostrich;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * The superclass of all ostrich horse models. There are different models for different layers of
 * armor. This class is responsible only for animations.
 *
 * @author CrowsOfWar
 */
public abstract class ModelOstrichHorse extends ModelBase {

	/**
	 * Get the neck box to be animated
	 */
	protected abstract ModelRenderer getNeck();

	protected abstract ModelRenderer getLeftLeg();

	protected abstract ModelRenderer getRightLeg();

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
								  float headPitch, float scaleFactor, Entity ostrich) {

		getNeck().rotateAngleY = (float) (toRadians(netHeadYaw));
		getNeck().rotateAngleX = (float) (toRadians(headPitch) + sin(limbSwing * 0.2) * 0.05);

		getLeftLeg().rotateAngleX = (float) (sin(limbSwing * 0.6) * limbSwingAmount * 0.5 +
				0.35);
		getRightLeg().rotateAngleX = (float) (sin(limbSwing * 0.6 + Math.PI) * limbSwingAmount
				* 0.5 + 0.35);

	}

}
