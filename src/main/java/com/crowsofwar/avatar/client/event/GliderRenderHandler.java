package com.crowsofwar.avatar.client.event;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.client.model_loaders.obj.ObjLoader;
import com.crowsofwar.avatar.client.model_loaders.obj.ObjModel;
import com.crowsofwar.avatar.item.ItemHangGliderBase;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.helper.GliderHelper;
import com.crowsofwar.avatar.item.IGlider;
import com.crowsofwar.avatar.util.helper.GliderPlayerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.crowsofwar.avatar.config.ConfigGlider.GLIDER_CONFIG;

@SideOnly(Side.CLIENT)
public class GliderRenderHandler {


    //==================================================Rotating the Player to a Flying Position (Horizontal)=====================================

    private boolean needToPop = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(RenderPlayerEvent.Pre event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer playerEntity = (EntityPlayer) event.getEntity();
            if (GliderHelper.getIsGliderDeployed((EntityPlayer) event.getEntity())) { //if gliderBasic deployed
                if (!GliderPlayerHelper.shouldBeGliding(playerEntity)) return; //don't continue if player is not flying
                if (Minecraft.getMinecraft().currentScreen instanceof GuiInventory) return; //don't rotate if the player rendered is in an inventory
                setRotationThirdPersonPerspective(event.getEntityPlayer(), event.getPartialRenderTick());
                //AvatarUtils.setRotationFromPosition(event.getEntityPlayer(), new Vec3d(event.getX(), event.getY(), event.getZ()));//rotate player to flying position
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onRender(RenderPlayerEvent.Post event) {
        if (this.needToPop) {
            this.needToPop = false;
            GlStateManager.popMatrix();
        }
    }

    //=============================================================Rendering In-World for 1st Person Perspective==================================================

    /**
     * For rendering as a perspective projection in-world, as opposed to the slightly odd looking orthogonal projection above
     */
    @SubscribeEvent
    public void onRenderOverlay(RenderWorldLastEvent event){
        if (GLIDER_CONFIG.enableRenderingFPP && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) { //rendering enabled and first person perspective
            EntityPlayer playerEntity = Minecraft.getMinecraft().player;
            if (GliderHelper.getIsGliderDeployed(playerEntity)) { //if gliderBasic deployed
                if (GliderPlayerHelper.shouldBeGliding(playerEntity)) { //if flying
                    renderGliderFirstPersonPerspective(event); //render hang gliderBasic above head
                }
            }
        }
    }

    //The model to display
    private final ObjModel modelGlider = ObjLoader.load(ItemHangGliderBase.MODEL_GLIDER_RL);

    /**
     * Renders the gliderBasic above the player
     *
     * @param event - the render world event
     */
    private void renderGliderFirstPersonPerspective(RenderWorldLastEvent event){

        EntityPlayer entityPlayer = Minecraft.getMinecraft().player;
        ItemStack gliderStack = GliderHelper.getGlider(Minecraft.getMinecraft().player);
        if (gliderStack == null || gliderStack.isEmpty()) return; //just in case the other null check don't work somehow, return
        ResourceLocation resourceLocation = ((IGlider)gliderStack.getItem()).getModelTexture(gliderStack);
        //set the rotation correctly for fpp
        setRotationFirstPersonPerspective(entityPlayer, event.getPartialTicks());
        //set the correct lighting
        setLightingBeforeRendering(entityPlayer, event.getPartialTicks());
        //render the glider model
//        GlStateManager.enableTexture2D();
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation); //bind texture
        GlStateManager.pushMatrix();
        GlStateManager.translate(0,0, 1);
        modelGlider.renderAll();
        GlStateManager.pushMatrix();
    }

    private void setLightingBeforeRendering(EntityPlayer player, float partialTicks) {
        GlStateManager.enableLighting();

        int i = player.getBrightnessForRender();
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        Minecraft.getMinecraft().entityRenderer.enableLightmap();

    }

    /**
     * Sets the rotation of the hang gliderBasic to work for first person rendering in-world.
     *
     * @param player - the player
     * @param partialTicks - the partial ticks
     */
    private void setRotationFirstPersonPerspective(EntityPlayer player, float partialTicks) {
        //Handles gliders scale rotation and translation for first person perspective
        double interpolatedYaw = (player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks);
        //rotate the gliderBasic to the same orientation as the player is facing
        GlStateManager.rotate((float) -interpolatedYaw, 0, 1, 0);
        //rotate the gliderBasic so it is forwards facing, as it should be
        GlStateManager.rotate(180F, 0, 1, 0);
        GlStateManager.rotate(90F, 1, 0, 0);
        //move up to correct position (above player's head)
        GlStateManager.translate(0, GLIDER_CONFIG.gliderVisibilityFPPShiftAmount, 0);
        GlStateManager.translate(0, 0, -3f);

        //move away if sneaking
        if (player.isSneaking())
            GlStateManager.translate(0, 0, -1 * GLIDER_CONFIG.shiftSpeedVisualShift); //subtle speed effect (makes gliderBasic smaller looking)

        boolean isAirbender = BendingData.get(player).getAllBending().contains(BendingStyles.get("airbending"));
        if(Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() && isAirbender)
        {
            GlStateManager.translate(0, 1 * GLIDER_CONFIG.airbenderHeightGain, 0); //subtle speed effect (makes gliderBasic smaller looking)
        }
    }

    private void setRotationThirdPersonPerspective(EntityPlayer player, float partialTicks) {
        player.limbSwingAmount = 0;
        GlStateManager.pushMatrix();
        GlStateManager.rotate(-player.rotationYawHead, 0, 1, 0);
        float interpolatedPitch = (player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks) + 90;
        GlStateManager.rotate(interpolatedPitch, 1, 0, 0);
        float interpolatedYaw = (player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) - partialTicks);
        GlStateManager.rotate(interpolatedYaw, 0, 1, 0);

        this.needToPop = true; //mark the matrix to pop
    }


    //================================================================Miscellaneous===========================================


    /**
     * Disable the offhand rendering if the player has a gliderBasic deployed (and is holding a gliderBasic)
     *
     * @param event - the render hand event
     */
    @SubscribeEvent
    public void onHandRender(RenderSpecificHandEvent event){
        EntityPlayer player = AvatarMod.proxy.getClientPlayer();
        if (GLIDER_CONFIG.disableOffhandRenderingWhenGliding || GLIDER_CONFIG.disableHandleBarRenderingWhenGliding) { //configurable
            if (GliderHelper.getIsGliderDeployed(player)) { //if gliderBasic deployed
                if (GLIDER_CONFIG.disableHandleBarRenderingWhenGliding) event.setCanceled(true);
                else if (GLIDER_CONFIG.disableOffhandRenderingWhenGliding) {
                    if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof IGlider && !((IGlider) player.getHeldItemMainhand().getItem()).isBroken(player.getHeldItemMainhand())) { //if holding a deployed hang gliderBasic
                        if (event.getHand() == EnumHand.OFF_HAND) { //offhand rendering
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Fix mouse wheel scroll on deployed glider to changes active item away from the glider not undeploying it.
     *
     * @param event - mouse event, fires before the slot is changed
     */
    @SubscribeEvent
    public void onScroll(MouseEvent event) {

        // Mouse Wheel
        int wheelState = event.getDwheel();

        // Mouse wheel scrolled
        if (wheelState != 0) {
            EntityPlayer player = AvatarMod.proxy.getClientPlayer();
            // Player has a deployed glider
            if (GliderHelper.getIsGliderDeployed(player)) {
                //Undeploy it
                GliderHelper.setIsGliderDeployed(player, false);
            }
        }

    }


}
