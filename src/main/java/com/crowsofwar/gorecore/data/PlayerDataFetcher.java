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

import java.util.UUID;

import com.crowsofwar.gorecore.GoreCore;
import com.crowsofwar.gorecore.util.AccountUUIDs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Responsible for retrieving player data from an arbitrary storing mechanism.
 * Implementations will generally return the player data upon success and null
 * upon failure.
 * 
 * @param <T>
 *            Type of player data
 * 
 * @author CrowsOfWar
 */
public interface PlayerDataFetcher<T extends PlayerData> {
	
	/**
	 * Retrieves player data from that world and with the given Minecraft
	 * Account UUID.
	 * 
	 * @param world
	 *            World to fetch from
	 * @param accountId
	 *            UUID of the player
	 * 
	 * @see AccountUUIDs
	 */
	T fetch(World world, UUID accountId);
	
	/**
	 * Retrieves player data from that world and with the given player name.
	 * Internally looks up the account UUID of the player.
	 * 
	 * @param world
	 *            World to fetch from
	 * @param playerName
	 *            UUID of the player
	 */
	default T fetch(World world, String playerName) {
		if (world == null) throw new IllegalArgumentException("Cannot get player-data with null World");
		if (playerName == null)
			throw new IllegalArgumentException("Cannot get player-data with null player name");
		AccountUUIDs.AccountID result = AccountUUIDs.getUUID(playerName);
		if (result.isResultSuccessful()) {
			return fetch(world, result.getUUID());
		} else {
			GoreCore.LOGGER.warn(
					"Unable to find player " + playerName + "'s UUID for PD fetch: " + result.getOutcome());
			return null;
		}
	}
	
	/**
	 * Retrieves player data from that world and with the given player entity.
	 * Internally looks up the account UUID of the player.
	 * 
	 * @param world
	 *            World to fetch from
	 * @param playerName
	 *            UUID of the player
	 */
	default T fetch(EntityPlayer player) {
		if (player == null) throw new IllegalArgumentException("Cannot get Player-Data for null player");
		return fetch(player.worldObj, player.getName());
	}
	
}
