package com.crowsofwar.avatar.client.render.ostrich;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * The superclass of all ostrich horse models. There are different models for different layers of
 * armor. This class is responsible only for animations.
 *
 * @author CrowsOfWar
 */
public abstract class ModelOstrichHorse extends ModelBase
{

	/**
	 * Get the neck box to be animated
	 */
	protected abstract ModelRenderer getNeck();

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
								  float headPitch, float scaleFactor, Entity ostrich) {

		getNeck().rotateAngleY = (float) Math.toRadians(netHeadYaw);
		getNeck().rotateAngleX = (float) Math.toRadians(headPitch);

	}

}
