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
