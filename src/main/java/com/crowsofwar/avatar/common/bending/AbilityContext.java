package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketCStatusControl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Information when an ability is executed. Only is used server-side.
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
		AvatarMod.network.sendTo(new PacketCStatusControl(control), (EntityPlayerMP) playerEntity);
	}
	
}
