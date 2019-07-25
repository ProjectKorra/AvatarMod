package com.crowsofwar.avatar.glider.client.event;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.glider.api.helper.GliderHelper;
import com.crowsofwar.avatar.glider.api.item.IGlider;
import com.crowsofwar.avatar.glider.client.model.ModelGlider;
import com.crowsofwar.avatar.glider.common.config.ConfigHandler;
import com.crowsofwar.avatar.glider.common.helper.OpenGliderPlayerHelper;
import com.crowsofwar.avatar.server.AvatarKeybindingServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.swing.text.JTextComponent;

@SideOnly(Side.CLIENT)
public class ClientEventHandler extends Gui {


    //==================================================Rotating the Player to a Flying Position (Horizontal)=====================================

    private boolean needToPop = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(RenderPlayerEvent.Pre event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer playerEntity = (EntityPlayer) event.getEntity();
            if (GliderHelper.getIsGliderDeployed((EntityPlayer) event.getEntity())) { //if gliderBasic deployed
                if (!OpenGliderPlayerHelper.shouldBeGliding(playerEntity)) return; //don't continue if player is not flying
                if (Minecraft.getMinecraft().currentScreen instanceof GuiInventory) return; //don't rotate if the player rendered is in an inventory
                rotateToHorizontal(event.getEntityPlayer(), event.getX(), event.getY(), event.getZ()); //rotate player to flying position
                this.needToPop = true; //mark the matrix to pop
            }
        }
    }

    /**
     * Makes the player's body rotate visually to be flat, parallel to the ground (e.g. like superman flies).
     *
     * @param playerEntity - the player to rotate
     * @param x - player's x pos
     * @param y - player's y pos
     * @param z - player's z pos
     */
    private void rotateToHorizontal(EntityPlayer playerEntity, double x, double y, double z){

        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        double interpolatedYaw = (playerEntity.prevRotationYaw + (playerEntity.rotationYaw - playerEntity.prevRotationYaw) * partialTicks);

        GlStateManager.pushMatrix();
        //7. Set position back to normal
        GlStateManager.translate(x, y, z);
        //6. Redo yaw rotation
        GlStateManager.rotate((float) -interpolatedYaw, 0, 1, 0);
        //5. Move back to (0, 0, 0)
        GlStateManager.translate(0, playerEntity.height / 2f, 0);
        //4. Rotate about x, so player leans over forward (z direction is forwards)
        GlStateManager.rotate(90, 1, 0, 0);
        //3. So we rotate around the centre of the player instead of the bottom of the player
        GlStateManager.translate(0, -playerEntity.height / 2f, 0);
        //2. Undo yaw rotation (this will make the player face +z (south)
        GlStateManager.rotate((float) interpolatedYaw, 0, 1, 0);
        //1. Set position to (0, 0, 0)
        GlStateManager.translate(-x, -y, -z);

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
        if (ConfigHandler.enableRenderingFPP && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) { //rendering enabled and first person perspective
            EntityPlayer playerEntity = Minecraft.getMinecraft().player;
            if (GliderHelper.getIsGliderDeployed(playerEntity)) { //if gliderBasic deployed
                if (OpenGliderPlayerHelper.shouldBeGliding(playerEntity)) { //if flying
                    renderGliderFirstPersonPerspective(event); //render hang gliderBasic above head
                }
            }
        }
    }

    //The model to display
    private final ModelGlider modelGlider = new ModelGlider();
//    private final ModelBars modelBars = new ModelBars();

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
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation); //bind texture

        //push matrix
        GlStateManager.pushMatrix();
        //set the rotation correctly for fpp
        setRotationFirstPersonPerspective(entityPlayer, event.getPartialTicks());
        //set the correct lighting
        setLightingBeforeRendering(entityPlayer, event.getPartialTicks());
        //render the gliderBasic
        modelGlider.render(entityPlayer, entityPlayer.limbSwing, entityPlayer.limbSwingAmount, entityPlayer.ticksExisted, entityPlayer.rotationYawHead, entityPlayer.rotationPitch, 1);
        //render the bars
//        Minecraft.getMinecraft().getTextureManager().bindTexture(ModelBars.MODEL_GLIDER_BARS_RL); //bind texture
//        modelBars.render(entityPlayer, entityPlayer.limbSwing, entityPlayer.limbSwingAmount, entityPlayer.getAge(), entityPlayer.rotationYawHead, entityPlayer.rotationPitch, 1);
        //pop matrix
        GlStateManager.popMatrix();

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
        double interpolatedYaw = (player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks);
        //rotate the gliderBasic to the same orientation as the player is facing
        GlStateManager.rotate((float) -interpolatedYaw, 0, 1, 0);
        //rotate the gliderBasic so it is forwards facing, as it should be
        GlStateManager.rotate(180F, 0, 1, 0);
        GlStateManager.rotate(90F, 1, 0, 0);
        //move up to correct position (above player's head)
        GlStateManager.translate(0, ConfigHandler.gliderVisibilityFPPShiftAmount, 0);
        GlStateManager.translate(0, 0, -3f);

        //move away if sneaking
        if (player.isSneaking())
            GlStateManager.translate(0, 0, -1 * ConfigHandler.shiftSpeedVisualShift); //subtle speed effect (makes gliderBasic smaller looking)

        boolean isAirbender = BendingData.get(player).getAllBending().contains(BendingStyles.get("airbending"));
        if(Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() && isAirbender)
        {
            GlStateManager.translate(0, 1 * ConfigHandler.airbenderHeightGain, 0); //subtle speed effect (makes gliderBasic smaller looking)
        }
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
        if (ConfigHandler.disableOffhandRenderingWhenGliding || ConfigHandler.disableHandleBarRenderingWhenGliding) { //configurable
            if (GliderHelper.getIsGliderDeployed(player)) { //if gliderBasic deployed
                if (ConfigHandler.disableHandleBarRenderingWhenGliding) event.setCanceled(true);
                else if (ConfigHandler.disableOffhandRenderingWhenGliding) {
                    if (player.getHeldItemMainhand() != null && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof IGlider && !((IGlider) player.getHeldItemMainhand().getItem()).isBroken(player.getHeldItemMainhand())) { //if holding a deployed hang gliderBasic
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
