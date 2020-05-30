package com.crowsofwar.avatar.api.helper;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.api.capabilities.CapabilityHelper;
import com.crowsofwar.avatar.api.capabilities.IAdvancedGliderCapabilityHandler;
import com.crowsofwar.avatar.api.item.IGlider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GliderHelper {


    //ToDo: getIGlider/setIGlider
    //return itemstack or IGlider?

    /**
     * Get the gliderBasic used, contains all the stats/modifiers of it.
     * Should only be needed when {@link IAdvancedGliderCapabilityHandler#getIsPlayerGliding} is true.
     * See {@link IGlider} for details.
     * Currently only gets the currently held item of the player when they have it deployed.
     *
     * @return - the IGlider the player is using, null if not using any.
     */
    public static ItemStack getGlider(EntityPlayer player) {
        IAdvancedGliderCapabilityHandler capability = CapabilityHelper.getGliderCapability(player);

        //if gliderBasic deployed
        if (capability != null && capability.getIsGliderDeployed()) {

            //if player holding a gliderBasic
            if (player != null && player.getHeldItemMainhand() != null && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof IGlider) {

                //return that held gliderBasic
                return player.getHeldItemMainhand();
            }
        }
        else
            AvatarLog.error("Cannot get gliderBasic used, gliderBasic capability not present.");
        return null;
    }

    /**
     * Wrapper method for {@link IAdvancedGliderCapabilityHandler#getIsPlayerGliding()}, taking into account capabilities.
     *
     * @param player - the player to check
     * @return - True if gliding, False otherwise (includes no capability)
     */
    public static boolean getIsPlayerGliding(EntityPlayer player) {
        IAdvancedGliderCapabilityHandler capability = CapabilityHelper.getGliderCapability(player);
        if (capability != null)
            return capability.getIsPlayerGliding();
        else
            AvatarLog.error("Cannot get player gliding status, gliderBasic capability not present.");
        return false;
    }

    /**
     * Wrapper method for {@link IAdvancedGliderCapabilityHandler#setIsPlayerGliding(boolean)}, taking into account capabilities.
     *
     * @param player - the player to check
     * @param isGliding - the gliding state to set
     */
    public static void setIsPlayerGliding(EntityPlayer player, boolean isGliding) {
        IAdvancedGliderCapabilityHandler capability = CapabilityHelper.getGliderCapability(player);
        if (capability != null)
            capability.setIsPlayerGliding(isGliding);
        else
            AvatarLog.error("Cannot set player gliding, gliderBasic capability not present.");
    }

    /**
     * Wrapper method for {@link IAdvancedGliderCapabilityHandler#getIsGliderDeployed()}, taking into account capabilities.
     *
     * @param player - the player to check
     * @return - True if deployed, False otherwise (includes no capability)
     */
    public static boolean getIsGliderDeployed(EntityPlayer player) {
        IAdvancedGliderCapabilityHandler capability = CapabilityHelper.getGliderCapability(player);
        if (capability != null) {
            return capability.getIsGliderDeployed();
        } else {
            AvatarLog.error("Cannot get gliderBasic deployment status, gliderBasic capability not present.");
        }
        return false;
    }

    /**
     * Wrapper method for {@link IAdvancedGliderCapabilityHandler#setIsGliderDeployed(boolean)}, taking into account capabilities.
     *
     * @param player - the player to check
     * @param isDeployed - the gliderBasic deployment state to set
     */
    public static void setIsGliderDeployed(EntityPlayer player, boolean isDeployed) {
        IAdvancedGliderCapabilityHandler capability = CapabilityHelper.getGliderCapability(player);
        if (capability != null)
            capability.setIsGliderDeployed(isDeployed);
        else
            AvatarLog.error("Cannot set gliderBasic deployed, gliderBasic capability not present.");
    }

}
