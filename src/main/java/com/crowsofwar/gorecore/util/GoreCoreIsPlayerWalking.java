package com.crowsofwar.gorecore.util;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import com.crowsofwar.gorecore.GoreCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Detects if players are walking or not.
 * 
 * @author CrowsOfWar
 */
public class GoreCoreIsPlayerWalking {
	
	/**
	 * Maps player account UUIDs to their walk distance on the previous tick
	 */
	private final Map<UUID, Integer> previousWalkDistance;
	
	/**
	 * Maps player account UUIDs to whether they are walking or not
	 */
	private final Map<UUID, Boolean> isWalking;
	
	public GoreCoreIsPlayerWalking() {
		previousWalkDistance = new WeakHashMap<UUID, Integer>();
		isWalking = new WeakHashMap<UUID, Boolean>();
		FMLCommonHandler.instance().bus().register(this);
	}
	
	/**
	 * Update the status for that player - whether he/she is walking.
	 */
	@SubscribeEvent
	public void updatePlayerWalking(PlayerTickEvent event) {
		UUID playerUUID = GoreCorePlayerUUIDs.getUUIDPerformance(event.player.getCommandSenderName());
		if (event.side == Side.SERVER && !event.player.isSprinting()) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			isWalking.put(playerUUID, prevWalkDistance(playerUUID) != getStat(player, StatList.distanceWalkedStat));
			previousWalkDistance.put(playerUUID, getStat(player, StatList.distanceWalkedStat));
		} else if (event.side == Side.CLIENT) {
			isWalking.put(playerUUID, GoreCore.proxy.isPlayerWalking(event.player));
			previousWalkDistance.put(playerUUID, -1);
		}
	}
	
	public int getStat(EntityPlayerMP player, StatBase stat) {
		return player.func_147099_x().writeStat(stat);
	}
	
	/**
	 * Get whether the specified player is walking. On the client, this will always return false for
	 * player entities who are not the person who is playing Minecraft.
	 */
	public boolean isWalking(EntityPlayer player) {
		Boolean b = isWalking.get(GoreCorePlayerUUIDs.getUUIDPerformance(player.getCommandSenderName()));
		return b != null && b.booleanValue();
	}
	
	private int prevWalkDistance(UUID playerUUID) {
		return previousWalkDistance.containsKey(playerUUID) ? previousWalkDistance.get(playerUUID) : -1;
	}
	
	public static GoreCoreIsPlayerWalking getWalkDetectorForCurrentSide() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? GoreCore.walkDetectorClient : GoreCore.walkDetectorServer;
	}
	
	/**
	 * <p>
	 * Get whether the specified player is walking. On the client, this will always return false for
	 * player entities who are not the person who is playing Minecraft.
	 * </p>
	 * 
	 * <p>
	 * This uses the current side's walk detector.
	 * </p>
	 * 
	 * @see #isWalking(player)
	 */
	public static boolean isWalkingStatic(EntityPlayer player) {
		return getWalkDetectorForCurrentSide().isWalking(player);
	}
	
}
