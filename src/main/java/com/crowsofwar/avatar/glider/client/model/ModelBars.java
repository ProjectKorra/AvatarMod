package com.crowsofwar.avatar.glider.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;

public class ModelBars extends ModelBase {

    private ArrayList<ModelRenderer> parts;
    public static final ResourceLocation MODEL_GLIDER_BARS_RL = new ResourceLocation(MOD_ID, "textures/models/bars.png");

    public ModelBars() {
        textureWidth = 32;
        textureHeight = 32;

        parts = new ArrayList<>();

        ModelRenderer main = new ModelRenderer(this, 0, 0);
        main.addBox(-10, 14, 5, 20, 2, 2);
        main.setRotationPoint(0, 0, 0);
        parts.add(main);

//        ModelRenderer leftBar = new ModelRenderer(this, 0, 8);
//        leftBar.addBox(-1.5F, -1, -0.5F, 3, 2, 1);
//        leftBar.setRotationPoint(0, 1.5F, -1.5F);
//        parts.add(leftBar);
//
//        ModelRenderer rightBar = new ModelRenderer(this, 0, 11);
//        rightBar.addBox(-1.5F, -1F, -0.5F, 3, 2, 1);
//        rightBar.setRotationPoint(0, .5F, -1F);
//        parts.add(rightBar);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        for (ModelRenderer part : parts) {
            part.render(scale);
        }

    }

    /**
     * Set all the details of the gliderBasic to render.
     */
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn){

//        EntityPlayer player = (EntityPlayer) entityIn;
//
//        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F); //set it to the back (no rotation)
//
//        GlStateManager.rotate(ONGROUND_ROTATION, 1, 0, 0); //on same plane as player
//        GlStateManager.rotate(180F, 0, 2, 0); //front facing
//        if (player.isSneaking())
//            GlStateManager.translate(0, -0.5, 0); //move to on the back (more away than fpp)
//        else
//            GlStateManager.translate(0, -0.35, 0); //move to on the back (quite close)
//
//        if (!GliderPlayerHelper.shouldBeGliding(player)) {
//            GlStateManager.scale(0.9, 0.9, 0.8); //scale slightly smaller
//            GlStateManager.translate(0, 0, -.5); // move up if on ground
//        }

    }


}
