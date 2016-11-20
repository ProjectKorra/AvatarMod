package com.crowsofwar.avatar.common.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.avatar.common.network.packets.AvatarPacket;

import io.netty.buffer.ByteBuf;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class PacketModularData extends AvatarPacket<PacketModularData> {
	
	private Set<Networker.Key> changed;
	private Map<Networker.Key, DataTransmitter> transmitters;
	private Map<Networker.Key, Object> currentData;
	
	private ByteBuf buf;
	
	public PacketModularData() {
		this.changed = new HashSet<>();
		this.transmitters = new HashMap<>();
		this.currentData = new HashMap<>();
	}
	
	/**
	 * @param changed
	 * @param transmitters
	 * @param currentData
	 */
	public PacketModularData(Networker networker) {
		this.changed = networker.changed;
		this.transmitters = networker.transmitters;
		this.currentData = networker.currentData;
	}
	
	/**
	 * Copy the networker's registered Transmitters so that they can be used
	 * when decoding the packet.
	 */
	public void expectData(Networker networker) {
		this.transmitters = networker.transmitters;
		this.currentData = networker.currentData;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.buf = buf;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		for (Networker.Key key : changed) {
			transmitters.get(key).write(buf, currentData.get(key));
		}
	}
	
}
