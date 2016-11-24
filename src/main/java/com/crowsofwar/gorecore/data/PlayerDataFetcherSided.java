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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Player data fetcher class which hands off the functionality to a delegate. (One for each side)
 * 
 * @author CrowsOfWar
 */
public class PlayerDataFetcherSided<T extends GoreCorePlayerData> implements PlayerDataFetcher<T> {
	
	private PlayerDataFetcher<T> clientDelegate, serverDelegate;
	
	public PlayerDataFetcherSided(PlayerDataFetcher<T> client, PlayerDataFetcher<T> server) {
		clientDelegate = client;
		serverDelegate = server;
	}
	
	private PlayerDataFetcher<T> getDelegate() {
		return FMLCommonHandler.instance().getEffectiveSide().isClient() ? clientDelegate : serverDelegate;
	}
	
	@Override
	public T fetch(EntityPlayer player, String errorMessage) {
		return getDelegate().fetch(player, errorMessage);
	}
	
	@Override
	public T fetch(World world, String playerName, String errorMessage) {
		return getDelegate().fetch(world, playerName, errorMessage);
	}
	
	@Override
	public T fetchPerformance(EntityPlayer player) {
		return getDelegate().fetchPerformance(player);
	}
	
	@Override
	public T fetchPerformance(World world, String playerName) {
		return getDelegate().fetchPerformance(world, playerName);
	}
	
}
