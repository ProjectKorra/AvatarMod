package com.crowsofwar.gorecore.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.crowsofwar.gorecore.util.GoreCoreNBTUtil;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;

/**
 * A world data class which comes equipped with the ability to save and load player data.
 * 
 * @param T
 *            The type of your player data
 * 
 * @author CrowsOfWar
 */
public abstract class GoreCoreWorldDataPlayers<T extends GoreCorePlayerData> extends GoreCoreWorldData {
	
	private Map<UUID, GoreCorePlayerData> players;
	
	public GoreCoreWorldDataPlayers(String key) {
		super(key);
		this.players = new HashMap<UUID, GoreCorePlayerData>();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.players = GoreCoreNBTUtil.readMapFromNBT(nbt, GoreCorePlayerData.MAP_USER, "PlayerData",
				new Object[] {}, new Object[] { playerDataClass(), this });
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		GoreCoreNBTUtil.writeMapToNBT(nbt, players, GoreCorePlayerData.MAP_USER, "PlayerData");
		return nbt;
	}
	
	/**
	 * Gets the player data for that player, creating it if necessary.
	 * 
	 * @param player
	 *            The UUID of the player to get data for
	 * @return Player data for that player
	 */
	public T getPlayerData(UUID player) {
		if (players.containsKey(player)) {
			T data = getPlayerDataWithoutCreate(player);
			if (getWorld() != null)
				data.setPlayerEntity(GoreCorePlayerUUIDs.findPlayerInWorldFromUUID(getWorld(), player));
			return data;
		} else {
			System.out.println("New player data for " + player);
			T data = createNewPlayerData(player);
			players.put(player, data);
			if (getWorld() != null)
				data.setPlayerEntity(GoreCorePlayerUUIDs.findPlayerInWorldFromUUID(getWorld(), player));
			saveChanges();
			return data;
		}
	}
	
	/**
	 * Gets the player data for the player. If the player data has not been created, then this will
	 * return null.
	 * 
	 * @param player
	 *            The UUID of the player to get data for
	 * @return Player data for the player, or null if it does not exist
	 */
	public T getPlayerDataWithoutCreate(UUID player) {
		T data = (T) players.get(player);
		if (data.getPlayerEntity() == null) {
			data.setPlayerEntity(GoreCorePlayerUUIDs.findPlayerInWorldFromUUID(getWorld(), player));
		}
		return data;
	}
	
	public abstract Class<? extends GoreCorePlayerData> playerDataClass();
	
	private T createNewPlayerData(UUID player) {
		try {
			
			EntityPlayer playerEntity = GoreCorePlayerUUIDs.findPlayerInWorldFromUUID(getWorld(), player);
			if (playerEntity == null)
				System.out.println("WARNING: playerEntity was null while creating new player data");
			GoreCorePlayerData data = playerDataClass()
					.getConstructor(GoreCoreDataSaver.class, UUID.class, EntityPlayer.class)
					.newInstance(this, player, playerEntity);
			return (T) data;
			
		} catch (Exception e) {
			FMLLog.warning("GoreCore> Found an error when trying to make new player data!");
			e.printStackTrace();
			return null;
		}
	}
	
}
