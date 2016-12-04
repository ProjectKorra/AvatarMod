/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.gorecore.data;

import net.minecraft.world.World;

/**
 * Gets world data for a given world. This will be moved into the dedicated
 * GoreCore project soon.
 * 
 * @author CrowsOfWar
 */
public interface WorldDataFetcher<T extends WorldData> {
	
	/**
	 * Get world data for that world.
	 * 
	 * @param world
	 *            The world
	 * @return The mod's world data for that world
	 */
	T getWorldData(World world);
	
}
