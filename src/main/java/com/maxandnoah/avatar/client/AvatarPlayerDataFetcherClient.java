package com.maxandnoah.avatar.client;

import com.maxandnoah.avatar.AvatarInfo;
import com.maxandnoah.avatar.AvatarMod;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;
import com.maxandnoah.avatar.common.network.packets.PacketSRequestData;

import crowsofwar.gorecore.data.GoreCoreModPlayerDataFetcherChecklist;
import crowsofwar.gorecore.data.GoreCorePlayerData;
import crowsofwar.gorecore.data.GoreCorePlayerDataCreationHandler;
import crowsofwar.gorecore.data.GoreCorePlayerDataFetcher;
import crowsofwar.gorecore.data.GoreCorePlayerDataFetcher.FetchDataResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class AvatarPlayerDataFetcherClient implements GoreCoreModPlayerDataFetcherChecklist<AvatarPlayerData> {
	
	private static final GoreCorePlayerDataCreationHandler onCreate = new GoreCorePlayerDataCreationHandler() {
		@Override
		public void onClientPlayerDataCreated(GoreCorePlayerData data) {
			AvatarMod.network.sendToServer(new PacketSRequestData(data.getPlayerID()));
		}
	};
	
	public static final AvatarPlayerDataFetcherClient instance = new AvatarPlayerDataFetcherClient();
	
	private AvatarPlayerDataFetcherClient() {}
	
	@Override
	public FetchDataResult getData(EntityPlayer player) {
		return GoreCorePlayerDataFetcher.ClientFetcher.getClientData(AvatarPlayerData.class, AvatarInfo.MOD_ID, player,
				onCreate);
	}
	
	@Override
	public FetchDataResult getData(World world, String playerName) {
		return GoreCorePlayerDataFetcher.ClientFetcher.getClientData(AvatarPlayerData.class, AvatarInfo.MOD_ID, playerName,
				onCreate);
	}
	
	@Override
	public AvatarPlayerData getDataQuick(EntityPlayer player, String errorMessage) {
		return (AvatarPlayerData) GoreCorePlayerDataFetcher.ClientFetcher.getDataQuick(AvatarPlayerData.class,
				AvatarInfo.MOD_ID, player, errorMessage, onCreate);
	}
	
	@Override
	public AvatarPlayerData getDataQuick(World world, String playerName, String errorMessage) {
		return (AvatarPlayerData) GoreCorePlayerDataFetcher.ClientFetcher.getDataQuick(AvatarPlayerData.class,
				AvatarInfo.MOD_ID, playerName, errorMessage, onCreate);
	}
	
	@Override
	public AvatarPlayerData getDataPerformance(EntityPlayer player) {
		return (AvatarPlayerData) GoreCorePlayerDataFetcher.ClientFetcher.getDataPerformance(AvatarPlayerData.class,
				AvatarInfo.MOD_ID, player, onCreate);
	}
	
	@Override
	public AvatarPlayerData getDataPerformance(World world, String playerName) {
		return (AvatarPlayerData) GoreCorePlayerDataFetcher.ClientFetcher.getDataPerformance(AvatarPlayerData.class,
				AvatarInfo.MOD_ID, playerName, onCreate);
	}
	
}
