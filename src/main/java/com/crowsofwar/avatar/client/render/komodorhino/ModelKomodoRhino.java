package com.crowsofwar.avatar.client.render.komodorhino;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import static java.lang.Math.cos;
import static java.lang.Math.toRadians;

/**
 * The superclass of all komodo rhino models. There are different models for different layers of
 * armor. This class is responsible only for animations.
 *
 * @author CrowsOfWar / talhanation
 */
public abstract class ModelKomodoRhino extends ModelBase {

    /**
     * Get the neck box to be animated
     */
    protected abstract ModelRenderer getNeck();

    protected abstract ModelRenderer getBackLeftLeg();

    protected abstract ModelRenderer getFrontLeftLeg();

    protected abstract ModelRenderer getBackRightLeg();

    protected abstract ModelRenderer getFrontRightLeg();

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                                  float headPitch, float scaleFactor, Entity ostrich) {

        getNeck().rotateAngleY = (float) (toRadians(netHeadYaw));
        getNeck().rotateAngleX = (float) (toRadians(headPitch));

        getFrontRightLeg().rotateAngleX = getBackLeftLeg().rotateAngleX  = (float) (cos(limbSwing * 0.36667f ) * 1.4 * limbSwingAmount * 0.3);

        getFrontLeftLeg().rotateAngleX = getBackRightLeg().rotateAngleX  = (float) (cos(limbSwing * 0.36667f + Math.PI ) * 1.4 * limbSwingAmount * 0.3 );

    }

}
