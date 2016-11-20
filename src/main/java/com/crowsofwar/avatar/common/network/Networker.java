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
	
	final Map<Key, DataTransmitter> transmitters;
	final Map<Key, Object> currentData;
	final Set<Key> changed;
	final Set<Key> allKeys;
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
		System.out.println("New networker, server? " + server);
	}
	
	/**
	 * Registers the key to this networker, along with its current data and
	 * transmitter. The data must be marked changed by calling
	 * {@link #markChanged(Key)}.
	 * <p>
	 * In order to properly call markChanged later, the instance of the key must
	 * not be changed, but the actual data can be a different object.
	 * 
	 * @param data
	 * @param transmitter
	 * @param key
	 */
	public <T> void register(T data, DataTransmitter<T> transmitter, Key key) {
		transmitters.put(key, transmitter);
		currentData.put(key, data);
		allKeys.add(key);
	}
	
	public <T> void markChanged(Key key, T data) {
		if (!allKeys.contains(key))
			throw new IllegalArgumentException("Invalid key- no data was registered with Key " + key);
		if (server) {
			System.out.println("marked " + key + " changed to " + data);
			changed.add(key);
			currentData.put(key, data);
		}
	}
	
	public <T> void changeAndSync(Key key, T data) {
		markChanged(key, data);
		sendUpdated();
	}
	
	public void sendUpdated() {
		if (server) {
			
			PacketModularData packet = packetCreator.apply(this);
			AvatarMod.network.sendToAll(packet);
			
		}
	}
	
	public void sendAll() {
		if (server) {
			PacketModularData packet = packetCreator.apply(this);
			packet.changed = allKeys;
			AvatarMod.network.sendToAll(packet);
		}
	}
	
	public interface Key {
		/**
		 * Gets the ID of this key.
		 * <p>
		 * IDs of each key should NOT be instance determined (e.g. with
		 * <code>hashCode()</code>) but instead pre-determined based off of the
		 * keys that might be sent at the same time. Therefore, it's OK to have
		 * 2 keys with the same ID, as long as they are never both used in 1
		 * networker.
		 */
		int id();
	}
	
}
