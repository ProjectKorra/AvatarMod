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
import java.util.function.Function;

import com.crowsofwar.gorecore.util.PlayerUUIDs;

import net.minecraft.world.World;

/**
 * Manages player data fetching on the server instance.
 * <p>
 * This is done through world data, which is responsible for actually storing,
 * saving, and instantiating data.
 * 
 * @param <T>
 * 
 * @author CrowsOfWar
 */
public class PlayerDataFetcherServer<T extends PlayerData> implements PlayerDataFetcher<T> {
	
	private final Function<World, WorldDataPlayers<T>> worldDataFetcher;
	
	public PlayerDataFetcherServer(Function<World, WorldDataPlayers<T>> worldDataFetcher) {
		this.worldDataFetcher = worldDataFetcher;
	}
	
	@Override
	public T fetch(World world, UUID accountId) {
		T data = worldDataFetcher.apply(world).getPlayerData(accountId);
		data.setPlayerEntity(PlayerUUIDs.findPlayerInWorldFromUUID(world, accountId));
		return data;
	}
	
}
