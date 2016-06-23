package com.maxandnoah.avatar.server;

import com.maxandnoah.avatar.common.AvatarCommonProxy;
import com.maxandnoah.avatar.common.IKeybindingManager;
import com.maxandnoah.avatar.common.gui.IAvatarGui;
import com.maxandnoah.avatar.common.network.PacketHandlerServer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.maxandnoah.avatar.common.network.IPacketHandler;

public class AvatarServerProxy implements AvatarCommonProxy {
	
	private AvatarKeybindingServer keys;
	
	@Override
	public void preInit() {
		keys = new AvatarKeybindingServer();
	}

	@Override
	public IKeybindingManager getKeyHandler() {
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
	
}
