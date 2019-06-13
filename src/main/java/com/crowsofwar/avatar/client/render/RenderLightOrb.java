package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityLightOrb;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderLightOrb extends Render<EntityLightOrb> {

    private ResourceLocation fill = new ResourceLocation("avatarmod", "textures/entity/sphere.png");
    private ResourceLocation halo = new ResourceLocation("avatarmod", "textures/entity/spherehalo.png");

    private CCModel model;

    private CCModel cubeModel, sphereModel;

    public RenderLightOrb(RenderManager manager) {
        super(manager);
        cubeModel = OBJParser.parseModels(new ResourceLocation("avatarmod", "models/cube.obj")).get("model");
        sphereModel = OBJParser.parseModels(new ResourceLocation("avatarmod", "models/hemisphere.obj")).get("model");
    }

    @Override
    public void doRender(EntityLightOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {

        model = entity.isSphere() ? sphereModel : cubeModel;

        Minecraft minecraft = Minecraft.getMinecraft();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        CCRenderState ccrs = CCRenderState.instance();
        TextureUtils.changeTexture(halo);

        GlStateManager.color(entity.getColorR(), entity.getColorG(), entity.getColorB(), entity.getColorA());

        double scale = entity.getOrbSize();

        double halocoord = 0.58 * scale;

        double dx = entity.posX - renderManager.viewerPosX;
        double dy = entity.posY - renderManager.viewerPosY;
        double dz = entity.posZ - renderManager.viewerPosZ;

        double xzlen = Math.sqrt(dx * dx + dz * dz);
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double yang = entity.isSphere() ? Math.atan2(xzlen, dy) * MathHelper.todeg : entity.rotationYaw;
        double xang = entity.isSphere() ? Math.atan2(dx, dz) * MathHelper.todeg : entity.rotationPitch;

        if (len > 16 || !entity.isSphere())
            halocoord = 0;

        GlStateManager.disableLighting();
        minecraft.entityRenderer.disableLightmap();

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);

            GlStateManager.rotate((float) xang, 0, 1, 0);
            GlStateManager.rotate((float) (yang + 90), 1, 0, 0);

            GlStateManager.pushMatrix();
            {
                GlStateManager.rotate(90, 1, 0, 0);

                GlStateManager.disableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.depthMask(false);

                buffer.begin(0x07, DefaultVertexFormats.POSITION_TEX);
                buffer.pos(-halocoord, 0.0, -halocoord).tex(0.0, 0.0).endVertex();
                buffer.pos(-halocoord, 0.0, halocoord).tex(0.0, 1.0).endVertex();
                buffer.pos(halocoord, 0.0, halocoord).tex(1.0, 1.0).endVertex();
                buffer.pos(halocoord, 0.0, -halocoord).tex(1.0, 0.0).endVertex();
                tessellator.draw();

                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
            }
            GlStateManager.popMatrix();

            TextureUtils.changeTexture(fill);

            GlStateManager.scale(scale, scale, scale);

            GlStateManager.disableCull();
            ccrs.startDrawing(entity.isSphere() ? 0x07 : 0x05, DefaultVertexFormats.POSITION_TEX_NORMAL);
            model.render(ccrs);
            ccrs.draw();
            GlStateManager.enableCull();

        }
        GlStateManager.popMatrix();

        minecraft.entityRenderer.enableLightmap();
        GlStateManager.enableLighting();

        GlStateManager.color(1, 1, 1, 1);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLightOrb entity) {
        return fill;
    }
}