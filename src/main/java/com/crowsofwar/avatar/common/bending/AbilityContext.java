package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import net.minecraft.entity.player.EntityPlayer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityContext {
	
	private final AvatarPlayerData data;
	private final EntityPlayer playerEntity;
	
	public AbilityContext(AvatarPlayerData data) {
		this.data = data;
		this.playerEntity = data.getPlayerEntity();
	}
	
	public AvatarPlayerData getData() {
		return data;
	}
	
	public EntityPlayer getPlayerEntity() {
		return playerEntity;
	}
	
	public void addStatusControl(StatusControl control) {
		
	}
	
}
