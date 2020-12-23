package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntitySandstorm;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author CrowsOfWar
 */
public class RenderSandstorm extends RenderModel<EntitySandstorm> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
            "textures/entity/sandstorm.png");
    private static final Random RANDOM = new Random();

    public RenderSandstorm(RenderManager renderManager) {
        super(renderManager, new ModelSandstorm());
    }

    @Override
    protected void performGlTransforms(EntitySandstorm entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.translate(0, -2.5, 0);
        GlStateManager.scale(3, 3, 3);

        float baseSize = 0.8f;
        float size = baseSize + entity.getStrength() * (1 - baseSize);
        GlStateManager.scale(size, size, size);

        float baseAlpha = 0.6f;
        GlStateManager.color(1, 1, 1, baseAlpha + entity.getStrength() * (1 - baseAlpha));

    }

    @Override
    public void doRender(EntitySandstorm entity, double x, double y, double z, float entityYaw,
                         float partialTicks) {

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        spawnParticles(entity);

    }

    /**
     * Spawns particles at the base of the sandstorm
     */
    private void spawnParticles(EntitySandstorm sandstorm) {

        World world = sandstorm.world;


    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntitySandstorm entity) {
        return TEXTURE;
    }
}
