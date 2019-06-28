package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityLightCylinder;
import com.crowsofwar.gorecore.util.Vector;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLightCylinder extends Render<EntityLightCylinder> {
    public RenderLightCylinder(RenderManager renderManager) {
        super(renderManager);
    }

    private static CCModel model = OBJParser.parseModels(new ResourceLocation("avatarmod", "models/cylinder.obj"))
            .get("model");
    private ResourceLocation TEXTURE;

    @Override
    public void doRender(EntityLightCylinder entity, double x, double y, double z, float entityYaw,
            float partialTicks) {

        TEXTURE = new ResourceLocation(entity.getTexture());

        EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
        CCRenderState ccrenderstate = CCRenderState.instance();
        TextureUtils.changeTexture(TEXTURE);

        double scale = entity.getCylinderSize();

        entity.rotationYaw = entity.getCylinderYaw();
        entity.rotationPitch = entity.getCylinderPitch();

        GlStateManager.pushMatrix();
        {
            GlStateManager.disableLighting();
            entityRenderer.disableLightmap();
            GlStateManager.disableCull();
            GlStateManager.color(1F, 1F, 1F, 0.2F);
            for (int i = 0; i < entity.getCylinderLenght(); i++) {
                GlStateManager.pushMatrix();
                {
                    Vector end = new Vector(x, y, z).plus(new Vector(entity.getLookVec()).times(i * (scale * 2)));

                    GlStateManager.translate(end.x(), end.y(), end.z());
                    GlStateManager.rotate((float) (entity.rotationYaw - entity.rotationYaw * 2), 0, 1, 0);
                    GlStateManager.rotate((float) (entity.rotationPitch + 90), 1, 0, 0);
                    GlStateManager.scale(scale, scale, scale);

                    ccrenderstate.startDrawing(0x05, DefaultVertexFormats.POSITION_TEX_NORMAL);
                    model.render(ccrenderstate);
                    ccrenderstate.draw();
                }
                GlStateManager.popMatrix();

            }
            GlStateManager.enableCull();
            entityRenderer.enableLightmap();
            GlStateManager.enableLighting();
        }
        GlStateManager.popMatrix();

        GlStateManager.color(1, 1, 1, 1);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLightCylinder entity) {
        return TEXTURE;
    }
}