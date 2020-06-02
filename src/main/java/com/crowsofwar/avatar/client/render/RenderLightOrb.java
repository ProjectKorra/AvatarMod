package com.crowsofwar.avatar.client.render;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.common.bending.fire.AbilityFlameStrike;
import com.crowsofwar.avatar.common.bending.fire.AbilityFlamethrower;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * @author Aang23
 */
public class RenderLightOrb extends Render<EntityLightOrb> {

    private static ResourceLocation fill_default = new ResourceLocation("avatarmod", "textures/entity/sphere.png");
    private static ResourceLocation halo = new ResourceLocation("avatarmod", "textures/entity/spherehalo.png");
    private ResourceLocation fill;

    public RenderLightOrb(RenderManager manager) {
        super(manager);
    }

    @Override
    public void doRender(EntityLightOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {

        if(entity.getType() == EntityLightOrb.EnumType.NOTHING) return;

        fill = entity.shouldUseCustomTexture() ? new ResourceLocation(entity.getTrueTexture()) : fill_default;

        boolean shouldCclRender = entity.isSphere();

        Minecraft minecraft = Minecraft.getMinecraft();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Only use CLL if present!
        if (shouldCclRender) TextureUtils.changeTexture(halo);
        else bindTexture(halo);

        if (entity.shouldUseCustomTexture()) {
            GlStateManager.color(1F, 1F, 1F, entity.getColorA());
        } else {
            GlStateManager.color(entity.getColorR(), entity.getColorG(), entity.getColorB(), entity.getColorA());
        }

        double scale = entity.getOrbSize();

        double haloCoord = 0.58 * scale;

        double dx = entity.posX - renderManager.viewerPosX;
        double dy = entity.posY - renderManager.viewerPosY;
        double dz = entity.posZ - renderManager.viewerPosZ;

        float ticks = entity.ticksExisted + partialTicks;
        float rotation = ticks * 20f;

        double lenghtXZ = Math.sqrt(dx * dx + dz * dz);
        double lenght = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double angleY = entity.isColorSphere() ? Math.atan2(lenghtXZ, dy) * MathHelper.todeg : entity.rotationYaw;
        double angleX = entity.isColorSphere() ? Math.atan2(dx, dz) * MathHelper.todeg : entity.rotationPitch;

        if (lenght > 16 || !entity.isColorSphere()) haloCoord = 0;

        GlStateManager.disableLighting();
        minecraft.entityRenderer.disableLightmap();

        GlStateManager.pushMatrix();
        {
            //Current translation is whack af
            GlStateManager.translate(x, y - 0.1/*+ entity.getOrbSize() / 2.7D**/, z);

            GlStateManager.rotate((float) angleX, 0, 1, 0);
            GlStateManager.rotate((float) (angleY + 90), 1, 0, 0);

            GlStateManager.pushMatrix();
            {
                GlStateManager.rotate(90, 1, 0, 0);

                GlStateManager.disableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.depthMask(false);

                buffer.begin(0x07, DefaultVertexFormats.POSITION_TEX);
                buffer.pos(-haloCoord, 0.0, -haloCoord).tex(0.0, 0.0).endVertex();
                buffer.pos(-haloCoord, 0.0, haloCoord).tex(0.0, 1.0).endVertex();
                buffer.pos(haloCoord, 0.0, haloCoord).tex(1.0, 1.0).endVertex();
                buffer.pos(haloCoord, 0.0, -haloCoord).tex(1.0, 0.0).endVertex();
                tessellator.draw();

                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
            }
            GlStateManager.popMatrix();

            // Only use CLL if present!
            if (shouldCclRender) TextureUtils.changeTexture(fill);
            else bindTexture(fill);

            GlStateManager.scale(scale, scale, scale);

          /*  if(entity.getAbility() instanceof AbilityFireball && entity.getEmittingEntity() != null && entity.getEmittingEntity() instanceof EntityFireball) {
                int amount = entity.getType() == EntityLightOrb.EnumType.TEXTURE_SPHERE ? 1 : 2;
                for (int i = 0; i < amount; i++) {
                    EntityFireball fireball = (EntityFireball) entity.getEmittingEntity();
                    World world = entity.world;
                    AxisAlignedBB boundingBox = fireball.getEntityBoundingBox();
                    double spawnX = boundingBox.minX + AvatarUtils.getRandomNumberInRange(1, 10) / 10F * (boundingBox.maxX - boundingBox.minX);
                    double spawnY = boundingBox.minY + AvatarUtils.getRandomNumberInRange(1, 10) / 10F * (boundingBox.maxY - boundingBox.minY);
                    double spawnZ = boundingBox.minZ + AvatarUtils.getRandomNumberInRange(1, 10) / 10F * (boundingBox.maxZ - boundingBox.minZ);
                    world.spawnParticle(AvatarParticles.getParticleFlames(), spawnX, spawnY, spawnZ, 0, 0, 0);
                }
            }**/

            if (entity.isSpinning() && entity.isSphere()) {
                //TODO: Data parameters for rotation amounts
                GlStateManager.rotate(rotation * 0.2F, 1, 0, 0);
                GlStateManager.rotate(rotation, 0, 1, 0);
                GlStateManager.rotate(rotation * 0.2F, 0, 0, 1);
            }

            GlStateManager.disableCull();
            if (entity.getType() == EntityLightOrb.EnumType.COLOR_CUBE || entity.getType() == EntityLightOrb.EnumType.TEXTURE_CUBE) {
                if (entity.isSpinning()) {
                    if (entity.getAbility() instanceof AbilityFlameStrike) {
                        RenderUtils.renderCube(0, 0, 0, 0d, 1d, 0d, 1d, 1f,
                                rotation * entity.getOrbSize() / 80F, rotation * entity.getOrbSize() / 40F, rotation * entity.getOrbSize() / 80F);
                    } else {
                        RenderUtils.renderCube(0, 0, 0, 0d, 1d, 0d, 1d, 1f,
                                rotation * 0.1F, rotation * 0.5F, rotation * 0.1F);
                    }
                } else {
                    RenderUtils.renderCube(0, 0, 0, 0d, 1d, 0d, 1d, 1F, 0, 0, 0);
                }
            } else {
                CCRenderState ccrenderstate = CCRenderState.instance();
                CCModel model = OBJParser.parseModels(new ResourceLocation("avatarmod", "models/hemisphere.obj")).get("model");
                if (!entity.shouldUseCustomTexture()) GlStateManager.color(entity.getColorR(), entity.getColorG(), entity.getColorB(), entity.getColorA());
                ccrenderstate.startDrawing(0x04, DefaultVertexFormats.POSITION_TEX_NORMAL);
                model.render(ccrenderstate);
                ccrenderstate.draw();

                GlStateManager.rotate(180, 1, 1, 0);
                if (!entity.shouldUseCustomTexture()) GlStateManager.color(entity.getColorR(), entity.getColorG(), entity.getColorB(), entity.getColorA());
                ccrenderstate.startDrawing(0x04, DefaultVertexFormats.POSITION_TEX_NORMAL);
                model.render(ccrenderstate);
                ccrenderstate.draw();
            }

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