package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public interface IBendingAbility {
	
	/**
	 * Returns whether this bending ability should be subscribed to an update tick event.
	 */
	boolean requiresUpdateTick();
	
	/**
	 * Tick this bending ability. Only called if {@link #requiresUpdateTick()} is true.
	 */
	default void tick() {}
	
	/**
	 * Execute this ability.
	 * 
	 * @param data
	 *            Player data to use.
	 */
	void execute(AvatarPlayerData data);
	
	/**
	 * Get the Id of this ability.
	 */
	int getId();
	
	/**
	 * Get the texture index of this bending ability. -1 for no texture.
	 */
	int getIconIndex();
	
	/**
	 * Returns whether this bending ability has an icon.
	 */
	default boolean hasTexture() {
		return getIconIndex() > -1;
	}
	
}
