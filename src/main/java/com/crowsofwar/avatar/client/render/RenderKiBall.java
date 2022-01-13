package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntityInfernalBall;
import com.crowsofwar.avatar.entity.EntityKiBall;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.crowsofwar.avatar.client.render.RenderUtils.drawSphere;

public class RenderKiBall extends Render<EntityKiBall> {

    public RenderKiBall(RenderManager renderManager) {
        super(renderManager);
    }

    //Copied from the forcefield class
    @Override
    public void doRender(@Nonnull EntityKiBall entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        //  OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        GlStateManager.translate(x, y + entity.height / 1.5, z);


        float latStep = (float) Math.PI / 10;
        float longStep = (float) Math.PI / 10;

        float pulse = MathHelper.sin((entity.ticksExisted + partialTicks) / 10f);

        float r = 40 / 255F, g = 240 / 255F + 0.025f * pulse, b = 255 / 255F;

        float radius = entity.width;
        float a = 1f;


        // Draw the inside first
        drawSphere(radius - 0.1f - 0.025f * pulse, latStep, longStep, true, r, g, b, a);
        drawSphere(radius - 0.1f - 0.025f * pulse, latStep, longStep, false, r, g, b, a * 0.7F);
        drawSphere(radius, latStep, longStep, false, r, g, b, a);


        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();

    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityKiBall entity) {
        return null;
    }
}