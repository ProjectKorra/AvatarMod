package com.crowsofwar.avatar.common.data;

import static com.crowsofwar.avatar.common.data.AvatarWorldData.getDataFromWorld;

import crowsofwar.gorecore.data.GoreCoreModPlayerDataFetcherChecklist;
import crowsofwar.gorecore.data.GoreCorePlayerDataFetcher;
import crowsofwar.gorecore.data.GoreCorePlayerDataFetcher.FetchDataResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class AvatarPlayerDataFetcherServer implements GoreCoreModPlayerDataFetcherChecklist<AvatarPlayerData> {
	
	public static AvatarPlayerDataFetcherServer instance =
			new AvatarPlayerDataFetcherServer();
	
	private AvatarPlayerDataFetcherServer() {}
	
	@Override
	public FetchDataResult getData(EntityPlayer player) {
		return GoreCorePlayerDataFetcher.ServerFetcher.
				getServerData(AvatarWorldData.getDataFromWorld(player.worldObj), player);
	}

	@Override
	public FetchDataResult getData(World world, String playerName) {

		return GoreCorePlayerDataFetcher.ServerFetcher.
				getServerData(getDataFromWorld(world), playerName);
	}

	@Override
	public AvatarPlayerData getDataQuick(EntityPlayer player, String errorMessage) {
		return (AvatarPlayerData) GoreCorePlayerDataFetcher.ServerFetcher.
				getDataQuick(getDataFromWorld(player.worldObj), player, errorMessage);
	}

	@Override
	public AvatarPlayerData getDataQuick(World world, String playerName, String errorMessage) {
		return (AvatarPlayerData) GoreCorePlayerDataFetcher.ServerFetcher.
				getDataQuick(getDataFromWorld(world), playerName, errorMessage);
	}

	@Override
	public AvatarPlayerData getDataPerformance(EntityPlayer player) {
		return (AvatarPlayerData) GoreCorePlayerDataFetcher.ServerFetcher.
				getDataPerformance(getDataFromWorld(player.worldObj), player);
	}

	@Override
	public AvatarPlayerData getDataPerformance(World world, String playerName) {
		return (AvatarPlayerData) GoreCorePlayerDataFetcher.ServerFetcher.
				getDataPerformance(getDataFromWorld(world), playerName);
	}
	
}
