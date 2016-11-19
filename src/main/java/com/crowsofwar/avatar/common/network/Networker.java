package com.crowsofwar.avatar.common.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Used by player data to keep track of which data changed and needs to be sent
 * across network.
 * 
 * @author CrowsOfWar
 */
public class Networker {
	
	private final Map<Key, DataTransmitter> transmitters;
	private final Map<Key, Object> currentData;
	private final Set<Key> changed;
	
	public Networker() {
		transmitters = new HashMap<>();
		currentData = new HashMap<>();
		changed = new HashSet<>();
	}
	
	public <T> void registerData(T data, DataTransmitter<T> transmitter, Key key) {
		transmitters.put(key, transmitter);
		currentData.put(key, data);
	}
	
	public void markChanged(Key key) {
		changed.add(key);
	}
	
	public void sendAll() {
		// somehow send packet here...
	}
	
	interface Key {
	}
	
}
