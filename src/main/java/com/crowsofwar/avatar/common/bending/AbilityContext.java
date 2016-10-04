package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketCStatusControl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

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
	
	public World getWorld() {
		return playerEntity == null ? null : playerEntity.worldObj;
	}
	
	public void addStatusControl(StatusControl control) {
		AvatarMod.network.sendTo(new PacketCStatusControl(control), (EntityPlayerMP) playerEntity);
		data.addStatusControl(control);
	}
	
}
