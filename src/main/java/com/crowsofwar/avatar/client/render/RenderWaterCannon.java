package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.sun.org.apache.regexp.internal.RE;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderWaterCannon extends RenderArc {
    private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
            "textures/entity/lightning-ribbon.png");

    private final ParticleSpawner particleSpawner;

    public RenderWaterCannon(RenderManager renderManager) {
        super(renderManager, false);
        enableFullBrightness();
        particleSpawner = new ClientParticleSpawner();
    }

    @Override
    public void doRender(Entity entity, double xx, double yy, double zz, float p_76986_8_,
                         float partialTicks) {

        EntityLightningArc arc = (EntityLightningArc) entity;
        renderArc(arc, partialTicks, 2f, 2f * arc.getSizeMultiplier());


    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

}


