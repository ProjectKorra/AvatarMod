package com.crowsofwar.avatar.api.helper;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.api.capabilities.CapabilityHelper;
import com.crowsofwar.avatar.api.capabilities.IGliderCapabilityHandler;
import com.crowsofwar.avatar.api.item.IGlider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GliderHelper {


    //ToDo: getIGlider/setIGlider
    //return itemstack or IGlider?

    /**
     * Get the gliderBasic used, contains all the stats/modifiers of it.
     * Should only be needed when {@link IGliderCapabilityHandler#getIsPlayerGliding} is true.
     * See {@link IGlider} for details.
     * Currently only gets the currently held item of the player when they have it deployed.
     *
     * @return - the IGlider the player is using, null if not using any.
     */
    public static ItemStack getGlider(EntityPlayer player) {
        IGliderCapabilityHandler cap = CapabilityHelper.getGliderCapability(player);

        //if gliderBasic deployed
        if (cap != null && cap.getIsGliderDeployed()) {

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
     * Wrapper method for {@link IGliderCapabilityHandler#getIsPlayerGliding()}, taking into account capabilities.
     *
     * @param player - the player to check
     * @return - True if gliding, False otherwise (includes no capability)
     */
    public static boolean getIsPlayerGliding(EntityPlayer player) {
        IGliderCapabilityHandler cap = CapabilityHelper.getGliderCapability(player);
        if (cap != null)
            return cap.getIsPlayerGliding();
        else
            AvatarLog.error("Cannot get player gliding status, gliderBasic capability not present.");
        return false;
    }

    /**
     * Wrapper method for {@link IGliderCapabilityHandler#setIsPlayerGliding(boolean)}, taking into account capabilities.
     *
     * @param player - the player to check
     * @param isGliding - the gliding state to set
     */
    public static void setIsPlayerGliding(EntityPlayer player, boolean isGliding) {
        IGliderCapabilityHandler cap = CapabilityHelper.getGliderCapability(player);
        if (cap != null)
            cap.setIsPlayerGliding(isGliding);
        else
            AvatarLog.error("Cannot set player gliding, gliderBasic capability not present.");
    }

    /**
     * Wrapper method for {@link IGliderCapabilityHandler#getIsGliderDeployed()}, taking into account capabilities.
     *
     * @param player - the player to check
     * @return - True if deployed, False otherwise (includes no capability)
     */
    public static boolean getIsGliderDeployed(EntityPlayer player) {
        IGliderCapabilityHandler cap = CapabilityHelper.getGliderCapability(player);
        if (cap != null)
            return cap.getIsGliderDeployed();
        else
            AvatarLog.error("Cannot get gliderBasic deployment status, gliderBasic capability not present.");
        return false;
    }

    /**
     * Wrapper method for {@link IGliderCapabilityHandler#setIsGliderDeployed(boolean)}, taking into account capabilities.
     *
     * @param player - the player to check
     * @param isDeployed - the gliderBasic deployment state to set
     */
    public static void setIsGliderDeployed(EntityPlayer player, boolean isDeployed) {
        IGliderCapabilityHandler cap = CapabilityHelper.getGliderCapability(player);
        if (cap != null)
            cap.setIsGliderDeployed(isDeployed);
        else
            AvatarLog.error("Cannot set gliderBasic deployed, gliderBasic capability not present.");
    }

}
