package com.crowsofwar.gorecore.data;

/**
 * Handles new player data being created on the client side.
 * 
 * @param <T>
 *            Class of the player data
 * 
 * @author CrowsOfWar
 */
public interface PlayerDataCreationHandler<T extends GoreCorePlayerData> {
	
	/**
	 * Called when client player data is created
	 * 
	 * @param data
	 *            The player data that just was created
	 */
	public void onClientPlayerDataCreated(T data);
	
}
