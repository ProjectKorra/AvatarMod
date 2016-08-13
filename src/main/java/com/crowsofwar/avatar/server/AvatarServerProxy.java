package com.crowsofwar.avatar.server;

import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.IAvatarGui;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.PacketHandlerServer;

import crowsofwar.gorecore.data.PlayerDataFetcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class AvatarServerProxy implements AvatarCommonProxy {
	
	private AvatarKeybindingServer keys;
	
	@Override
	public void preInit() {
		keys = new AvatarKeybindingServer();
	}
	
	@Override
	public IControlsHandler getKeyHandler() {
		return keys;
	}
	
	@Override
	public IPacketHandler getClientPacketHandler() {
		return null;
	}
	
	@Override
	public double getPlayerReach() {
		return 0;
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public IAvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
	
	@Override
	public PlayerDataFetcher<AvatarPlayerData> getClientDataFetcher() {
		return null;
	}
	
}
