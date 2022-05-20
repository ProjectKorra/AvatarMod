package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntityAbyssBall;
import com.crowsofwar.avatar.util.AvatarUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.crowsofwar.avatar.client.render.RenderUtils.drawSphere;

public class RenderAbyssBall extends Render<EntityAbyssBall> {

    public RenderAbyssBall(RenderManager renderManager) {
        super(renderManager);
    }

    //Copied from the forcefield class
    @Override
    public void doRender(@Nonnull EntityAbyssBall entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
      //  GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_COLOR, GlStateManager.DestFactor.ZERO);
        //  OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        GlStateManager.translate(x, y + entity.height / 1.5, z);


        float latStep = (float) Math.PI / 20;
        float longStep = (float) Math.PI / 20;

        float pulse = MathHelper.sin((entity.ticksExisted + partialTicks) / 10f);

        float r = getClrRand(), g = getClrRand() + 0.025f * pulse, b = getClrRand();

        float radius = entity.width;
        float a = 0.75F;


        // Draw the inside first
        drawSphere(radius - 0.1f - 0.025f * pulse, latStep, longStep, true, 0.01F, 0.01F, 0.01F, a);
        drawSphere(radius - 0.1f - 0.025f * pulse, latStep, longStep, false, 0.01F, 0.01F, 0.01F, a * 0.7F);
        drawSphere(radius, latStep, longStep, false, 0.01F, 0.01F, 0.01F, a);


        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();

    }

    private float getClrRand() {
        return AvatarUtils.getRandomNumberInRange(1, 25) / 255F;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityAbyssBall entity) {
        return null;
    }
}