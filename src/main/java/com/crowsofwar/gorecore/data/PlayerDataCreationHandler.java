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
