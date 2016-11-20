package com.crowsofwar.avatar.common.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	
	public Networker(boolean server, Class<? extends PacketModularData> dataPacket) {
		transmitters = new HashMap<>();
		currentData = new HashMap<>();
		changed = new HashSet<>();
		allKeys = new HashSet<>();
		this.server = server;
		this.packet = dataPacket;
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
			changed.add(key);
			currentData.put(key, data);
		}
	}
	
	public void sendUpdated() {
		// somehow send packet here...
		if (server) {
			try {
				
				PacketModularData packet = this.packet.getConstructor(Networker.class).newInstance(this);
				AvatarMod.network.sendToAll(packet);
				
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public interface Key {
	}
	
}
