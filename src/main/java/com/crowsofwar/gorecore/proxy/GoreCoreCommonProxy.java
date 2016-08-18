package com.crowsofwar.gorecore.proxy;

import java.io.File;

import com.crowsofwar.gorecore.util.GoreCoreIsPlayerWalking;

import net.minecraft.entity.player.EntityPlayer;

public class GoreCoreCommonProxy {
	
	private File uuidCacheFile;
	private File minecraftDirectory;
	
	public GoreCoreCommonProxy() {
		uuidCacheFile = createUUIDCacheFile();
		minecraftDirectory = createMinecraftDir();
	}
	
	public final File getUUIDCacheFile() {
		return uuidCacheFile;
	}
	
	protected File createUUIDCacheFile() {
		return new File("GoreCore_ServerUUIDCache.txt");
	}
	
	public final File getMinecraftDir() {
		return minecraftDirectory;
	}
	
	protected File createMinecraftDir() {
		return new File(".");
	}
	
	/**
	 * Returns whether that person is currently walking. This only works for the person who is
	 * playing Minecraft.
	 */
	public boolean isPlayerWalking(EntityPlayer player) {
		return false;
	}
	
	public GoreCoreIsPlayerWalking initPlayerWalkingClient() {
		return null;
	}
	
	public void sideSpecifics() {
		
	}
	
	public String translate(String key, Object... args) {
		return String.format(StatCollector.translateToLocal(key), args);
	}
	
	public EntityPlayer getClientSidePlayer() {
		return null;
	}
	
}
