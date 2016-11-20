package com.crowsofwar.avatar.common.network;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class PlayerDataContext implements Context {
	
	private final AvatarPlayerData data;
	
	/**
	 * @param data
	 */
	public PlayerDataContext(AvatarPlayerData data) {
		this.data = data;
	}
	
	public AvatarPlayerData getData() {
		return data;
	}
	
}
