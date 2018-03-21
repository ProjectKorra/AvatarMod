/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.gorecore.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;

/**
 * <p>
 * Contains utility methods for getting player's account UUIDs. Account UUIDs
 * are the UUIDs on Mojang, and entity UUIDs are the UUIDs gotten from
 * Entity#getUniqueID().
 * </p>
 * 
 * @author CrowsOfWar
 * @author Mahtaran
 */
public final class AccountUUIDs {
	/**
	 * Don't try to understand this.
	 * Ok, so actually it works like this: select first 8 characters, then 4, 4, 4 and finally 12. This is the UUID format.
	 */
	private static final Pattern DASHLESS_PATTERN = Pattern.compile("^([A-Fa-f0-9]{8})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{12})$");
	
	/**
	 * <p>
	 * Finds the player in the world whose account has the given UUID.
	 * </p>
	 * 
	 * <p>
	 * This is different from <code>world.func_152378_a(playerID)</code> in that
	 * the world's method uses the player's entity ID, while this method uses
	 * the player's account ID.
	 * </p>
	 * 
	 * @param playerID
	 *            The UUID of the player to find
	 * @param world
	 *            The world to look for the player in
	 * @return The player with that UUID
	 */
	public static EntityPlayer findEntityFromUUID(World world, UUID playerID) {
		for (int i = 0; i < world.playerEntities.size(); i++) {
			UUID accountId = getId(world.playerEntities.get(i).getName());
			if (accountId.equals(playerID)) {
				return world.playerEntities.get(i);
			}
		}
		return null;
	}
	
	/**
	 * <p>
	 * Gets the UUID of the player with the given username. If it exists in the
	 * cache, the UUID will be obtained via the cache; otherwise, a HTTP request
	 * will be made to obtain the UUID.
	 * </p>
	 * 
	 * @param username
	 *            The username to get the UUID for
	 * @return The UUID result of the getting
	 */
	public static UUID getId(String username) {
		for (Map.Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
			if (entry.getValue().equalsIgnoreCase(username)) {
				return entry.getKey();
			}
    	}
		return requestId(username);
	}
	
	/**
	 * Sends a request to Mojang's API and get the player's UUID. Returns null
	 * if any error occurred.
	 */
	private static UUID requestId(String username) {
		try {
			String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
			
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			int responseCode = connection.getResponseCode();
			if (responseCode == 204) {
				GoreCore.LOGGER.warn("Attempted to get a UUID for player " + username
						+ ", but that account is not registered");
				return null;
			}
			
			if (responseCode != 200) {
				GoreCore.LOGGER.warn("Attempted to get a UUID for player " + username
						+ ", but the response code was unexpected (" + responseCode + ")");
				return null;
			}
			
			ByteArrayOutputStream response = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			InputStream inputStream = connection.getInputStream();
			while ((length = inputStream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return UUID.fromString(addDashes(JsonUtils.fromString(response.toString(), "id").getAsString()));				
		} catch (Exception e) {
			GoreCore.LOGGER.error("Unexpected error getting UUID for " + username, e);
			return null;
		}
	}
	
	/**
	 * Lookup the username based on the account ID. Returns null on errors.
	 * Warning: is not cached
	 */
	public static String getUsername(UUID id) {
		try {
			String idString = id.toString().replaceAll("-", "");
			String url = "https://api.mojang.com/user/profiles/" + idString + "/names";
			
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			int responseCode = connection.getResponseCode();
			if (responseCode == 204) {
				GoreCore.LOGGER.warn("Attempted to get a username for player " + id
						+ ", but that account is not registered");
				return null;
			}
			
			if (responseCode != 200) {
				GoreCore.LOGGER.warn("Attempted to get a username for player " + id
						+ ", but the response code was unexpected (" + responseCode + ")");
				return null;
			}
			
			ByteArrayOutputStream response = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			InputStream inputStream = connection.getInputStream();
			while ((length = inputStream.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			
			return JsonUtils.fromString(response.toString(), "name").getAsString();
		} catch (Exception e) {
			GoreCore.LOGGER.error("Unexpected error getting username for " + id, e);
			return null;
		}
	}
	
	/**
	 * Add dashes to a UUID. Method from SquirrelID
	 *
	 * <p>If dashes already exist, the same UUID will be returned.</p>
	 *
	 * @param uuid the UUID
	 * @return a UUID with dashes
	 * @throws IllegalArgumentException thrown if the given input is not actually an UUID
	 * @author sk89q
	 */
	public static String addDashes(String uuid) {
		uuid = uuid.replace("-", ""); // Remove dashes
		Matcher matcher = DASHLESS_PATTERN.matcher(uuid);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid UUID format");
		}
		return matcher.replaceAll("$1-$2-$3-$4-$5");
	}
}
