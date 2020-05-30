package com.crowsofwar.avatar.api.capabilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * This interface defines the contract to deal with the gliding status of a player.
 * Handled internally/exposed through capabilities.
 *
 * Acquire an instance of this using {@link net.minecraft.entity.Entity#getCapability(Capability, EnumFacing)}.
 * It should only ever be present on players.
 */
public interface IAdvancedGliderCapabilityHandler extends INBTSerializable<NBTTagCompound> {

    /**
     * Get the current gliding status of the player.
     * If True, it inherently must mean that the glider is deployed as well.
     *
     * @return - True if the player is gliding, False otherwise.
     */
    boolean getIsPlayerGliding();

    /**
     * Set the player's current gliding status.
     * If True, it inherently must mean that the glider is deployed as well.
     *
     * @param isPlayerGliding - True if the player is gliding, False otherwise.
     */
    void setIsPlayerGliding(boolean isPlayerGliding);

    /**
     * Get the current deployment status of the glider on the player.
     *
     * @return True is the player has a deployed glider, False otherwise.
     */
    boolean getIsGliderDeployed();

    /**
     * Set the player's glider's deployment status.
     *
     * @param isGliderDeployed - True if the glider is deployed, False otherwise.
     */
    void setIsGliderDeployed(boolean isGliderDeployed);

    /**
     * Sync the (above) data stored in the capability to a given player.
     *
     * @param player - the player to sync the data to.
     */
    void sync(EntityPlayerMP player);

}
