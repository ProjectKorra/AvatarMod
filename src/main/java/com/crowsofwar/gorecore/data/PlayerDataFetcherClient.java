package com.crowsofwar.gorecore.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.crowsofwar.gorecore.GoreCore;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs.ResultOutcome;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

@SideOnly(Side.CLIENT)
public class PlayerDataFetcherClient<T extends GoreCorePlayerData> implements PlayerDataFetcher<T> {
	
	private final Minecraft mc;
	/**
	 * Keeps track of client-side player data by mapping player UUID to player data.
	 */
	private Map<UUID, T> playerData = new HashMap<UUID, T>();
	private Class<T> dataClass;
	private PlayerDataCreationHandler<T> onCreate;
	/**
	 * Amount of ticks until check if player data can unload.
	 */
	private int ticksUntilCheck;
	
	/**
	 * Create a client-side player data fetcher.
	 * 
	 * @param dataClass
	 *            The class of your player data
	 */
	public PlayerDataFetcherClient(Class<T> dataClass) {
		this(dataClass, (data) -> {
		});// TODO fix newline when eclipse allows option to do that
	}
	
	/**
	 * Create a client-side player data fetcher.
	 * 
	 * @param dataClass
	 *            The class of your player data
	 * @param onCreate
	 *            A handler which will be invoked when a new instance of your player data was
	 *            created.
	 */
	public PlayerDataFetcherClient(Class<T> dataClass, PlayerDataCreationHandler<T> onCreate) {
		this.mc = Minecraft.getMinecraft();
		this.dataClass = dataClass;
		this.onCreate = onCreate;
		this.ticksUntilCheck = 20;
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private T createPlayerData(Class<T> dataClass, UUID playerID) {
		try {
			
			EntityPlayer player = GoreCorePlayerUUIDs.findPlayerInWorldFromUUID(mc.theWorld, playerID);
			return dataClass.getConstructor(GoreCoreDataSaver.class, UUID.class, EntityPlayer.class)
					.newInstance(new GoreCoreDataSaverDontSave(), playerID, player);
			
		} catch (Exception e) {
			GoreCore.LOGGER.warn("Found an error when trying to make new client-side player data!");
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public T fetch(EntityPlayer player, String errorMessage) {
		return fetch(player.worldObj, player.getCommandSenderName(), errorMessage);
	}
	
	@Override
	public T fetch(World world, String playerName, String errorMessage) {
		T data;
		
		GoreCorePlayerUUIDs.GetUUIDResult getUUID = GoreCorePlayerUUIDs.getUUID(playerName);
		GoreCorePlayerUUIDs.ResultOutcome error = getUUID.getResult();
		if (getUUID.isResultSuccessful()) {
			
			UUID playerID = getUUID.getUUID();
			
			data = playerData.get(playerID);
			if (data == null) {
				data = createPlayerData(dataClass, playerID);
				playerData.put(playerID, data);
				if (onCreate != null) onCreate.onClientPlayerDataCreated(data);
			}
			
		} else {
			
			getUUID.logError();
			data = null;
			
		}
		
		if (error == ResultOutcome.SUCCESS) {
			return data;
		} else {
			if (errorMessage != null) GoreCore.LOGGER.error("Error while retrieving player data- " + errorMessage);
			String log;
			switch (error) {
			case BAD_HTTP_CODE:
				log = "Unexpected HTTP code";
				break;
			case EXCEPTION_OCCURED:
				log = "Unexpected exception occurred";
				break;
			case USERNAME_DOES_NOT_EXIST:
				log = "Account is not registered";
				break;
			default:
				log = "Unexpected error: " + error;
				break;
			
			}
			
			return null;
			
		}
		
	}
	
	@Override
	public T fetchPerformance(EntityPlayer player) {
		return fetchPerformance(player.worldObj, player.getCommandSenderName());
	}
	
	@Override
	public T fetchPerformance(World world, String playerName) {
		T data;
		
		GoreCorePlayerUUIDs.GetUUIDResult getUUID = GoreCorePlayerUUIDs.getUUID(playerName);
		if (getUUID.isResultSuccessful()) {
			UUID playerID = getUUID.getUUID();
			data = playerData.get(playerID);
			if (data == null) {
				data = createPlayerData(dataClass, playerID);
				playerData.put(playerID, data);
				if (onCreate != null) onCreate.onClientPlayerDataCreated(data);
			}
			
		} else {
			data = null;
		}
		
		return data;
	}
	
	/**
	 * Player data cache is cleared when world is unloaded.
	 */
	@SubscribeEvent
	public void onUnloadWorld(WorldEvent.Unload e) {
		playerData.clear();
		System.out.println("All data decached");
	}
	
	/**
	 * Every second, check player data if it needs to unload.
	 */
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent e) {
		if (e.side == Side.CLIENT && e.phase == Phase.START && --ticksUntilCheck == 0) {
			ticksUntilCheck = 20;
			Iterator<UUID> iterator = playerData.keySet().iterator();
			while (iterator.hasNext()) {
				UUID playerId = iterator.next();
				T data = playerData.get(playerId);
				if (data.shouldBeDecached()) {
					System.out.println("data needed to be decached");
					iterator.remove();
				}
			}
		}
	}
	
}
