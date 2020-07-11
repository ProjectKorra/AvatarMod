package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntityLightCylinder;
import com.crowsofwar.gorecore.util.Vector;

import org.lwjgl.opengl.GL11;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
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

    private ResourceLocation TEXTURE;

    @Override
    public void doRender(EntityLightCylinder entity, double x, double y, double z, float entityYaw,
            float partialTicks) {

        boolean shouldCclRender = entity.getType() == EntityLightCylinder.EnumType.ROUND;

        TEXTURE = new ResourceLocation(entity.getTexture());

        EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;

        // Only use CLL if present!
        if (shouldCclRender) TextureUtils.changeTexture(TEXTURE);
        else bindTexture(TEXTURE);

        double scale = entity.getCylinderSize();

        entity.rotationYaw = entity.getCylinderYaw();
        entity.rotationPitch = entity.getCylinderPitch();

        GlStateManager.pushMatrix();
        {
            GlStateManager.disableLighting();
            entityRenderer.disableLightmap();
            GlStateManager.disableCull();
            GlStateManager.color(1F, 1F, 1F, entity.getColorA());
            int lenght = (int) entity.getCylinderLength();
            double lastLenght = entity.getCylinderLength() - lenght;
            for (int i = 0; i < lenght; i++) {
                GlStateManager.pushMatrix();
                {
                    Vector end = new Vector(x, y, z)
                            .plus(new Vector(entity.getLookVec()).times(i * (scale * (shouldCclRender ? 2 : 1))));

                    GlStateManager.translate(end.x(), end.y(), end.z());
                    GlStateManager.rotate(entity.rotationYaw - entity.rotationYaw * 2, 0, 1, 0);
                    GlStateManager.rotate(entity.rotationPitch + 90, 1, 0, 0);
                    GlStateManager.scale(scale, lenght == i + 1 ? scale * lastLenght : scale, scale);

                    if (entity.shouldSpin())
                       GlStateManager.rotate(entity.ticksExisted * entity.getDegreesPerSecond(), 0,0, 1);

                    if (shouldCclRender) {
                        CCModel model = OBJParser.parseModels(new ResourceLocation("avatarmod", "models/cylinder.obj")).get("model");
                        CCRenderState ccrenderstate = CCRenderState.instance();
                        ccrenderstate.startDrawing(0x05, DefaultVertexFormats.POSITION_TEX_NORMAL);
                        model.render(ccrenderstate);
                        ccrenderstate.draw();
                    } else {
                        Tessellator t = Tessellator.getInstance();
                        BufferBuilder vb = t.getBuffer();

                        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        vb.pos(0, 0, 0).tex(0F, 0F).endVertex();
                        vb.pos(1, 0, 0).tex(1F, 0F).endVertex();
                        vb.pos(1, 1, 0).tex(1F, 1F).endVertex();
                        vb.pos(0, 1, 0).tex(0F, 1F).endVertex();
                        t.draw();
                        GlStateManager.rotate(90f, 0, 1, 0);
                        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        vb.pos(0, 0, 0).tex(0F, 0F).endVertex();
                        vb.pos(1, 0, 0).tex(1F, 0F).endVertex();
                        vb.pos(1, 1, 0).tex(1F, 1F).endVertex();
                        vb.pos(0, 1, 0).tex(0F, 1F).endVertex();
                        t.draw();
                        GlStateManager.rotate(90f, 0, 1, 0);
                        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        vb.pos(0, 0, 1).tex(0F, 0F).endVertex();
                        vb.pos(-1, 0, 1).tex(1F, 0F).endVertex();
                        vb.pos(-1, 1, 1).tex(1F, 1F).endVertex();
                        vb.pos(0, 1, 1).tex(0F, 1F).endVertex();
                        t.draw();
                        GlStateManager.rotate(270f, 0, 1, 0);
                        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                        vb.pos(0, 0, 1).tex(0F, 0F).endVertex();
                        vb.pos(1, 0, 1).tex(1F, 0F).endVertex();
                        vb.pos(1, 1, 1).tex(1F, 1F).endVertex();
                        vb.pos(0, 1, 1).tex(0F, 1F).endVertex();
                        t.draw();
                    }
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