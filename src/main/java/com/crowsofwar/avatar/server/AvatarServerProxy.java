package com.crowsofwar.avatar.server;

import java.util.Set;

import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
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
	public AvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
	
	@Override
	public PlayerDataFetcher<AvatarPlayerData> getClientDataFetcher() {
		return null;
	}
	
	@Override
	public IThreadListener getClientThreadListener() {
		return null;
	}
	
	@Override
	public int getParticleAmount() {
		return 0;
	}
	
	@Override
	public void addStatusControl(StatusControl control) {}
	
	@Override
	public void removeStatusControl(StatusControl control) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Set<StatusControl> getAllStatusControls() {
		return null;
	}
	
}
