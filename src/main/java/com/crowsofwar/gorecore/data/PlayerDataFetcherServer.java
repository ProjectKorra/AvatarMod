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
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs.ResultOutcome;

import net.minecraft.entity.player.EntityPlayer;
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
	
	private final WorldDataFetcher<? extends WorldDataPlayers<T>> worldDataFetcher;
	
	public PlayerDataFetcherServer(WorldDataFetcher<? extends WorldDataPlayers<T>> worldDataFetcher) {
		this.worldDataFetcher = worldDataFetcher;
	}
	
	@Override
	public T fetch(EntityPlayer player, String errorMessage) {
		return fetch(player.worldObj, player.getName(), errorMessage);
	}
	
	@Override
	public T fetch(World world, String playerName, String errorMessage) {
		T data;
		GoreCorePlayerUUIDs.ResultOutcome error;
		
		GoreCorePlayerUUIDs.GetUUIDResult getUUID = GoreCorePlayerUUIDs.getUUID(playerName);
		if (getUUID.isResultSuccessful()) {
			
			data = worldDataFetcher.fetch(world).getPlayerData(getUUID.getUUID());
			error = getUUID.getResult();
			
		} else {
			
			getUUID.logError();
			data = null;
			error = getUUID.getResult();
			
		}
		
		if (error == ResultOutcome.SUCCESS) {
			data.setPlayerEntity(world.getPlayerEntityByName(playerName));
			return data;
		} else {
			if (errorMessage != null)
				GoreCore.LOGGER.error("Error while retrieving player data- " + errorMessage);
			String log;
			switch (error) {
				case BAD_HTTP_CODE:
					log = "Unexpected HTTP code";
					break;
				case EXCEPTION_OCCURED:
					log = "Unexpected exception occurred";
					break;
				case USERNAME_DOES_NOT_EXIST:
					log = "Account is not registered";
					break;
				default:
					log = "Unexpected error: " + error;
					break;
				
			}
			
			return null;
			
		}
		
	}
	
	@Override
	public T fetchPerformance(EntityPlayer player) {
		return fetchPerformance(player.worldObj, player.getName());
	}
	
	@Override
	public T fetchPerformance(World world, String playerName) {
		UUID res = GoreCorePlayerUUIDs.getUUIDPerformance(playerName);
		return res == null ? null : worldDataFetcher.fetch(world).getPlayerData(res);
	}
	
	public static interface WorldDataFetcher<T extends WorldData> {
		
		T fetch(World world);
		
	}
	
}
