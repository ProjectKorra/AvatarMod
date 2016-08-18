package com.crowsofwar.gorecore.client;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Adjusts the player's eye height. Attach it to the GoreCoreEntityRenderer by calling
 * {@link GoreCoreEntityRenderer#hook(GoreCorePlayerViewpointAdjuster)}.
 * 
 * @author CrowsOfWar
 */
public interface GoreCorePlayerViewpointAdjuster {
	
	/**
	 * Get the change in eye height for this player.
	 * 
	 * @param player
	 *            The player - will always be Minecraft#thePlayer
	 * @return The adjustment in eye height
	 */
	float getAdjustedEyeHeight(EntityPlayer player);
	
}
