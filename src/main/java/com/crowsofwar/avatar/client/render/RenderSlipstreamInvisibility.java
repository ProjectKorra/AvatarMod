package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.bending.bending.air.powermods.SlipstreamPowerModifier;
import com.crowsofwar.avatar.util.data.BendingData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class RenderSlipstreamInvisibility implements LayerRenderer<AbstractClientPlayer> {
    /**
     * Instance of the player renderer.
     */
    private final RenderPlayer playerRenderer;

    public RenderSlipstreamInvisibility(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }


    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (BendingData.getFromEntity(entitylivingbaseIn) != null) {
            BendingData data = BendingData.getFromEntity(entitylivingbaseIn);
            if (data != null && data.getPowerRatingManager(Airbending.ID) != null && data.getPowerRatingManager(Airbending.ID).hasModifier(SlipstreamPowerModifier.class)) {
                if (entitylivingbaseIn.getActivePotionEffects().contains(MobEffects.INVISIBILITY)) {
                    playerRenderer.getMainModel().setVisible(false);
                    
                }
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    public static void addLayer(){
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        try {
            RenderPlayer renderPlayer = ObfuscationReflectionHelper.getPrivateValue(RenderManager.class, renderManager, "playerRenderer", "field_178637_m");
            renderPlayer.addLayer(new RenderSlipstreamInvisibility(renderPlayer));
            System.out.println("added invis hook");
        } catch (Exception e) {
            AvatarLog.error("Could not add invis hook!");
        }
    }
}
