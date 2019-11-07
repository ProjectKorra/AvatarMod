package com.crowsofwar.avatar.client.render;


import com.crowsofwar.avatar.common.entity.mob.EntityBadgerMole;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderBadgerMole extends RenderLiving<EntityBadgerMole> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
            "textures/mob/badgermole.png");

    /**
     * @param renderManager
     */
    public RenderBadgerMole(RenderManager renderManager) {
        super(renderManager, new ModelBadgerMole(), 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBadgerMole entity) {
        return TEXTURE;
    }

}