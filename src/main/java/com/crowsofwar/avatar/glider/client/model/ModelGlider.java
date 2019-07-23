package com.crowsofwar.avatar.glider.client.model;

import com.crowsofwar.avatar.glider.common.helper.OpenGliderPlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;

public class ModelGlider extends ModelBase {

    public static final ResourceLocation MODEL_GLIDER_BASIC_TEXTURE_RL = new ResourceLocation(MOD_ID, "textures/models/blackstaff.png");
    public static final ResourceLocation MODEL_GLIDER_ADVANCED_TEXTURE_RL = new ResourceLocation(MOD_ID, "textures/models/blackstaff.png");

    private static final float QUAD_HALF_SIZE = 2.4f;
    private static final float ONGROUND_ROTATION = 90f;

    ModelRenderer Staff;
    ModelRenderer Left_small_wing;
    ModelRenderer Right_large_wing;
    ModelRenderer Left_large_wing;
    ModelRenderer Right_small_wing;

    public ModelGlider() {
        textureWidth = 64;
        textureHeight = 64;

        Staff = new ModelRenderer(this, 0, 0);
        Staff.addBox(0F, 0F, 0F, 1, 1, 40);
        Staff.setRotationPoint(0F, 7F, -17F);
        Staff.setTextureSize(64, 64);
        Staff.mirror = true;
        setRotation(Staff, 0F, 0F, 0F);
        Left_small_wing = new ModelRenderer(this, 0, 58);
        Left_small_wing.addBox(0F, 0F, 0F, 9, 0, 5);
        Left_small_wing.setRotationPoint(0F, 6.8F, 12F);
        Left_small_wing.setTextureSize(64, 64);
        Left_small_wing.mirror = true;
        setRotation(Left_small_wing, 0F, -0.4461433F, 0F);
        Left_small_wing.mirror = false;
        Right_large_wing = new ModelRenderer(this, -15, 0);
        Right_large_wing.addBox(0F, 0F, 0F, 20, 0, 15);
        Right_large_wing.setRotationPoint(-18F, 6.9F, -7F);
        Right_large_wing.setTextureSize(64, 64);
        Right_large_wing.mirror = true;
        setRotation(Right_large_wing, 0F, 0.4089647F, 0F);
        Left_large_wing = new ModelRenderer(this, 0, 42);
        Left_large_wing.addBox(0F, 0F, 0F, 20, 0, 15);
        Left_large_wing.setRotationPoint(1F, 6.8F, -15F);
        Left_large_wing.setTextureSize(64, 64);
        Left_large_wing.mirror = true;
        setRotation(Left_large_wing, 0F, -0.4089647F, 0F);
        Right_small_wing = new ModelRenderer(this, 0, 58);
        Right_small_wing.addBox(0F, 0F, 0F, 9, 0, 5);
        Right_small_wing.setRotationPoint(-7F, 6.9F, 16F);
        Right_small_wing.setTextureSize(64, 64);
        Right_small_wing.mirror = true;
        setRotation(Right_small_wing, 0F, 0.4461433F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Staff.render(f5);
        Left_small_wing.render(f5);
        Right_large_wing.render(f5);
        Left_large_wing.render(f5);
        Right_small_wing.render(f5);
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;

    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        GlStateManager.translate(0, -0.35, 0); //move to on the back (quite close)
//        GlStateManager.rotate(0,0,0,0);
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView > 0)
        {
//            GlStateManager.translate(0, -.35f, 0);
//            GlStateManager.rotate(-90,1,0,0);
//            GlStateManager.rotate(90, 0, 1, 0); Decent
//            GlStateManager.rotate(180, 1, 0, 0); No Good
            GlStateManager.rotate(180, 1F, 0, 0);
//            GlStateManager.rotate(90, 0, 0, 1);
        }

        if (!OpenGliderPlayerHelper.shouldBeGliding(Minecraft.getMinecraft().player)) {
            GlStateManager.scale(1.1, 1.1, 1.2); //scale slightly larger
//            GlStateManager.rotate(90,0,1,0);
//            GlStateManager.rotate(90,0,0,1);
//            GlStateManager.translate(0, 0, -.5); // move up if on ground

        }

    }

    /**
     * Set all the details of the gliderBasic to render.
     */
//    @Override
//    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn){
//
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
//        if (!OpenGliderPlayerHelper.shouldBeGliding(player)) {
//            GlStateManager.scale(0.9, 0.9, 0.8); //scale slightly smaller
//            GlStateManager.translate(0, 0, -.5); // move up if on ground
//        }
//
//    }

}
