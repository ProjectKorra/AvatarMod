package com.crowsofwar.gorecore.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs.GetUUIDResult;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * <p>
 * Keeps track of people who helped the mod by using an online text file.
 * </p>
 * 
 * @author CrowsOfWar
 */
public class GoreCoreHelperDetector {
	
	/**
	 * The name of the mod this is detecting helpers for.
	 */
	private String mod;
	
	/**
	 * Maps player usernames to their roles.
	 */
	private Map<String, String> playersUsername;
	
	/**
	 * Maps player UUIDs to their roles.
	 */
	private Map<UUID, String> playersUUID;
	
	/**
	 * Load usernames and put their roles in a map.
	 * 
	 * @param modName
	 *            The name of your mod
	 * @param url
	 *            The URL of your username list
	 */
	public GoreCoreHelperDetector(String modName, String url) {
		mod = modName;
		playersUsername = new HashMap<String, String>();
		playersUUID = new HashMap<UUID, String>();
		
		try {
			
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			int responseCode = connection.getResponseCode();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = br.readLine()) != null) {
				if (line.contains("=")) {
					String[] split = line.split("=", 2);
					GetUUIDResult res = GoreCorePlayerUUIDs.getUUID(split[0]);
					if (res.isResultSuccessful()) {
						// Only put in if it was successful to avoid weird problems with
						// playersUsername containing
						// the player data but playersUUID not!
						playersUsername.put(split[0], split[1]);
						playersUUID.put(res.getUUID(), split[1]);
					}
				}
			}
			br.close();
			
		} catch (Exception e) {
			FMLLog.severe("GoreCore> Error reading player username list! URL: " + url);
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Enable greeting of helpers when a player joins the server. Call this on startup.
	 */
	public void greetHelpers() {
		FMLCommonHandler.instance().bus().register(this);
	}
	
	/**
	 * Get the role of the given player, identified using his/her username.
	 */
	public String getRole(String playerName) {
		return playersUsername.get(playerName);
	}
	
	/**
	 * Get the role of the given player, identified using his/her UUID.
	 */
	public String getRole(UUID playerName) {
		return playersUUID.get(playerName);
	}
	
	/**
	 * <p>
	 * <strong>Don't call this unless you are cpw's code!</strong>
	 * </p>
	 * 
	 * <p>
	 * An event method to greet players on join. This is only registered if {@link #greetHelpers()}
	 * is called.
	 * </p>
	 */
	@SubscribeEvent
	public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		String role;
		if (!event.player.worldObj.isRemote && (role = getRole(event.player.getCommandSenderName())) != null) {
			List<EntityPlayer> players = event.player.worldObj.playerEntities;
			for (EntityPlayer p : players) {
				String key = "gc.greet." + role;
				String translated = StatCollector.translateToLocal(role);
				if (translated != key) p.addChatMessage(new ChatComponentTranslation(key, event.player.getCommandSenderName(), mod));
			}
		}
	}
	
}
