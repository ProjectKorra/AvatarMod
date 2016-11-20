package com.crowsofwar.avatar.common.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.crowsofwar.avatar.common.network.packets.AvatarPacket;

import io.netty.buffer.ByteBuf;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class PacketModularData<MSG extends PacketModularData> extends AvatarPacket<MSG> {
	
	Set<Networker.Key> changed;
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
	
	public Map<Networker.Key, Object> interpretData(Networker networker) {
		return interpretData(networker, null);
	}
	
	/**
	 * Uses the networker's transmitters to read the data. Returns the
	 * interpreted data as a map.
	 * 
	 * @param networker
	 *            the networker
	 * @param context
	 *            the context object. All transmitters are passed this context,
	 *            so make sure that they each have the same context requirement
	 */
	public Map<Networker.Key, Object> interpretData(Networker networker, Context context) {
		Map<Networker.Key, Object> out = new HashMap<>();
		Map<Networker.Key, DataTransmitter> transmitters = networker.transmitters;
		
		System.out.println("======== INTERPRETING");
		System.out.println("Transmitters: " + transmitters);
		System.out.println();
		
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			int keyId = buf.readInt();
			Networker.Key key = networker.allKeys.stream().filter(candidate -> candidate.id() == keyId)
					.collect(Collectors.toList()).get(0); // Find Key with the
															// id of keyId
			System.out.println("key " + key);
			Object read = transmitters.get(key).read(buf, context);
			System.out.println("got " + read);
			out.put(key, read);
		}
		return out;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.buf = buf;
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(changed.size());
		for (Networker.Key key : changed) {
			buf.writeInt(key.id());
			transmitters.get(key).write(buf, currentData.get(key));
		}
	}
	
	public ByteBuf getBuf() {
		return buf;
	}
	
}
