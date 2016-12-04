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

import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Player data fetcher class which delegates to a sided player data fetcher.
 * This is done by checking the current thread; works with integrated server.
 * 
 * @author CrowsOfWar
 */
public class PlayerDataFetcherSided<T extends PlayerData> implements PlayerDataFetcher<T> {
	
	private PlayerDataFetcher<T> clientDelegate, serverDelegate;
	
	public PlayerDataFetcherSided(PlayerDataFetcher<T> client, PlayerDataFetcher<T> server) {
		clientDelegate = client;
		serverDelegate = server;
	}
	
	@Override
	public T fetch(World world, UUID accountId) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT)
			return clientDelegate.fetch(world, accountId);
		else
			return serverDelegate.fetch(world, accountId);
	}
	
}
