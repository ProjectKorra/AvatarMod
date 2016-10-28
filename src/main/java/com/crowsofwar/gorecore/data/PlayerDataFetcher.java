package com.crowsofwar.gorecore.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface PlayerDataFetcher<T extends GoreCorePlayerData> {
	
	T fetch(EntityPlayer player, String errorMessage);
	
	T fetch(World world, String playerName, String errorMessage);
	
	T fetchPerformance(EntityPlayer player);
	
	T fetchPerformance(World world, String playerName);
	
}
