package com.crowsofwar.avatar.client.renderer;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.client.model_loaders.obj.ObjLoader;
import com.crowsofwar.avatar.client.model_loaders.obj.ObjModel;
import com.crowsofwar.avatar.item.ItemGliderBase;
import com.crowsofwar.avatar.util.helper.GliderHelper;
import com.crowsofwar.avatar.item.IGlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

import static com.crowsofwar.avatar.config.ConfigGlider.GLIDER_CONFIG;

public class LayerGlider implements LayerRenderer<AbstractClientPlayer> {

    /** Instance of the player renderer. */
    private final RenderPlayer playerRenderer;
    /** The model used by the gliderBasic. */
    private final ObjModel gliderModel;


    public LayerGlider(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
        gliderModel = ObjLoader.load(ItemGliderBase.MODEL_GLIDER_RL);
    }

    @Override
    @SubscribeEvent
    public void doRenderLayer(@Nonnull AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        //Handles glider render layers scale rotation and translation for third person perspective
        if (!entitylivingbaseIn.isInvisible() && GLIDER_CONFIG.enableRendering3PP) { //if not invisible and should render

            boolean gliding = GliderHelper.getIsGliderDeployed(entitylivingbaseIn); //get if gliding (to render or not)
            if (gliding) { //if there is one
                //bind texture of the current glider
                ItemStack gliderStack = GliderHelper.getGlider(entitylivingbaseIn);
                ResourceLocation resourceLocation = ((IGlider)gliderStack.getItem())
                        .getModelTexture(gliderStack);
                //binds the texture
//                GlStateManager.enableTexture2D();

                Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);

                GlStateManager.pushMatrix();
                GlStateManager.rotate(180f, 1, 0, 0);
                GlStateManager.scale(1.3, 1.3, 1.3);
                GlStateManager.translate(0, 0, -0.2);
                gliderModel.renderAll();
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
            AvatarLog.error("Could not add glider layer!");
        }
    }
}
