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

package com.crowsofwar.avatar.common.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.crowsofwar.avatar.AvatarMod;

/**
 * Used by player data to keep track of which data changed and needs to be sent
 * across network. Only used server-side.
 * 
 * @author CrowsOfWar
 */
public class Networker {
	
	final Map<Property, DataTransmitter> transmitters;
	final Map<Property, Object> currentData;
	final Set<Property> changed;
	final Set<Property> allKeys;
	private final boolean server;
	private final Class<? extends PacketModularData> packet;
	private final Function<Networker, ? extends PacketModularData> packetCreator;
	
	public <P extends PacketModularData> Networker(boolean server, Class<P> dataPacket,
			Function<Networker, P> packetCreator) {
		transmitters = new HashMap<>();
		currentData = new HashMap<>();
		changed = new HashSet<>();
		allKeys = new HashSet<>();
		this.server = server;
		this.packet = dataPacket;
		this.packetCreator = packetCreator;
	}
	
	/**
	 * Registers the key to this networker, along with its current data and
	 * transmitter. The data must be marked changed by calling
	 * {@link #markChanged(Property)}.
	 * <p>
	 * In order to properly call markChanged later, the instance of the key must
	 * not be changed, but the actual data can be a different object.
	 * 
	 * @param data
	 * @param transmitter
	 * @param key
	 */
	public <T> void register(T data, DataTransmitter<T, ?> transmitter, Property<T> key) {
		transmitters.put(key, transmitter);
		currentData.put(key, data);
		allKeys.add(key);
	}
	
	public <T> void markChanged(Property<T> key, T data) {
		if (!allKeys.contains(key))
			throw new IllegalArgumentException("Invalid key- no data was registered with Key " + key);
		if (server) {
			changed.add(key);
			currentData.put(key, data);
		}
	}
	
	public <T> void changeAndSync(Property<T> key, T data) {
		markChanged(key, data);
		sendUpdated();
	}
	
	public void sendUpdated() {
		if (server) {
			
			PacketModularData packet = packetCreator.apply(this);
			AvatarMod.network.sendToAll(packet);
			changed.clear();
			
		}
	}
	
	public void sendAll() {
		if (server) {
			PacketModularData packet = packetCreator.apply(this);
			packet.changed = allKeys;
			AvatarMod.network.sendToAll(packet);
		}
	}
	
	public void markAllChanged() {
		changed.addAll(allKeys);
	}
	
	public static class Property<T> {
		
		private final int id;
		
		public Property(int id) {
			this.id = id;
		}
		
		/**
		 * Gets the ID of this key.
		 * <p>
		 * IDs of each key should NOT be instance determined (e.g. with
		 * <code>hashCode()</code>) but instead pre-determined based off of the
		 * keys that might be sent at the same time. Therefore, it's OK to have
		 * 2 keys with the same ID, as long as they are never both used in 1
		 * networker.
		 */
		public int id() {
			return id;
		}
		
	}
	
}
