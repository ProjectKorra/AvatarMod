package com.crowsofwar.avatar.glider.client.renderer;

import com.crowsofwar.avatar.glider.api.helper.GliderHelper;
import com.crowsofwar.avatar.glider.api.item.IGlider;
import com.crowsofwar.avatar.glider.client.model.ModelGlider;
import com.crowsofwar.avatar.glider.common.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;

public class LayerGlider implements LayerRenderer<AbstractClientPlayer> {

    /** Instance of the player renderer. */
    private final RenderPlayer playerRenderer;
    /** The model used by the gliderBasic. */
    private final ModelGlider modelGlider = new ModelGlider();
//    private final ModelBars modelBars = new ModelBars();

    public LayerGlider(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(@Nonnull AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        //Handles glider render layers scale rotation and translation for third person perspective
        if (!entitylivingbaseIn.isInvisible() && ConfigHandler.enableRendering3PP) { //if not invisible and should render

            boolean gliding = GliderHelper.getIsGliderDeployed(entitylivingbaseIn); //get if gliding (to render or not)
            if (gliding) { //if there is one

                //bind texture of the current glider
                ItemStack gliderStack = GliderHelper.getGlider(entitylivingbaseIn);
                this.playerRenderer.bindTexture(((IGlider)gliderStack.getItem()).getModelTexture(gliderStack));

                //push matrix
                GlStateManager.pushMatrix();
                GlStateManager.rotate(270.0F, 1.0F, 0.0F, 0.0F);
//                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0, -.5f, 0);
                //set rotation angles of the glider and render it
                this.modelGlider.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);

                this.modelGlider.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                //pop matrix
                GlStateManager.popMatrix();
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
            renderPlayer.addLayer(new LayerGlider(renderPlayer));
            System.out.println("added glider layer");
        } catch (Exception e) {
            // failed to add layer
            //ToDo: Add logger class
        }
    }
}
