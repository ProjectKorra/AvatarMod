package com.crowsofwar.avatar.glider.client.model;

import com.crowsofwar.avatar.glider.common.helper.OpenGliderPlayerHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;

public class ModelGlider extends ModelBase {

    public static final ResourceLocation MODEL_GLIDER_BASIC_TEXTURE_RL = new ResourceLocation(MOD_ID, "textures/models/hang_glider.png");
    public static final ResourceLocation MODEL_GLIDER_ADVANCED_TEXTURE_RL = new ResourceLocation(MOD_ID, "textures/models/hang_glider_advanced.png");

    private static final float QUAD_HALF_SIZE = 2.4f;
    private static final float ONGROUND_ROTATION = 90f;

    public ModelGlider() {
        //empty, all calls are in render
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        GlStateManager.disableRescaleNormal();


        GlStateManager.enableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        //Bottom Face
        GlStateManager.glNormal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.glBegin(GL11.GL_QUADS);

        GlStateManager.glTexCoord2f(1, 1);
        GlStateManager.glVertex3f(QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE);

        GlStateManager.glTexCoord2f(0, 1);
        GlStateManager.glVertex3f(-QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE);

        GlStateManager.glTexCoord2f(0, 0);
        GlStateManager.glVertex3f(-QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE);

        GlStateManager.glTexCoord2f(1, 0);
        GlStateManager.glVertex3f(QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE);

        GlStateManager.glEnd();

        //Blend to make shading less powerful on bottom face
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1, 1, 1, 0.5F);

        //Bottom Face re-rendered
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F); //normal inverted
        GlStateManager.glBegin(GL11.GL_QUADS);

        GlStateManager.glTexCoord2f(1, 1);
        GlStateManager.glVertex3f(QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE);

        GlStateManager.glTexCoord2f(0, 1);
        GlStateManager.glVertex3f(-QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE);

        GlStateManager.glTexCoord2f(0, 0);
        GlStateManager.glVertex3f(-QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE);

        GlStateManager.glTexCoord2f(1, 0);
        GlStateManager.glVertex3f(QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE);

        GlStateManager.glEnd();

        //Stop Blending
        GlStateManager.disableBlend();

        //Top Face (normal)
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.glBegin(GL11.GL_QUADS);

        GlStateManager.glTexCoord2f(1, 0);
        GlStateManager.glVertex3f(QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE);

        GlStateManager.glTexCoord2f(0, 0);
        GlStateManager.glVertex3f(-QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE);

        GlStateManager.glTexCoord2f(0, 1);
        GlStateManager.glVertex3f(-QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE);

        GlStateManager.glTexCoord2f(1, 1);
        GlStateManager.glVertex3f(QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE);

        GlStateManager.glEnd();

        GlStateManager.disableBlend();

//        * Render face with correct normal
//                * Enable blending
//                * Set blend func to SRC_ALPHA, ONE_MINUS_SRC_ALPHA
//* Set color to [1, 1, 1, 0.5 (or something)]
//* Render face with opposite normal
//                * Disable blending


    }

    /**
     * Set all the details of the gliderBasic to render.
     */
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn){

        EntityPlayer player = (EntityPlayer) entityIn;

        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F); //set it to the back (no rotation)

        GlStateManager.rotate(ONGROUND_ROTATION, 1, 0, 0); //on same plane as player
        GlStateManager.rotate(180F, 0, 2, 0); //front facing
        if (player.isSneaking())
            GlStateManager.translate(0, -0.5, 0); //move to on the back (more away than fpp)
        else
            GlStateManager.translate(0, -0.35, 0); //move to on the back (quite close)

        if (!OpenGliderPlayerHelper.shouldBeGliding(player)) {
            GlStateManager.scale(0.9, 0.9, 0.8); //scale slightly smaller
            GlStateManager.translate(0, 0, -.5); // move up if on ground
        }

    }

}
