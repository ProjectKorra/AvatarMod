package com.crowsofwar.gorecore.data;

import net.minecraft.world.World;

/**
 * Gets world data for a given world. This will be moved into the dedicated GoreCore project soon.
 * 
 * @author CrowsOfWar
 */
public interface GoreCoreWorldDataFetcher<T extends GoreCoreWorldData> {
	
	/**
	 * Get world data for that world.
	 * 
	 * @param world
	 *            The world
	 * @return The mod's world data for that world
	 */
	T getWorldData(World world);
	
}
