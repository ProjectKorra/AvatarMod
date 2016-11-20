package com.crowsofwar.avatar.common.network;

import io.netty.buffer.ByteBuf;

/**
 * Used by the {@link Networker}, and is responsible for reading/writing the
 * data to the network.
 * 
 * @author CrowsOfWar
 */
public interface DataTransmitter<T> {
	
	/**
	 * Writes the <code>T</code> to network
	 */
	void write(ByteBuf buf, T t);
	
	/**
	 * Creates a new <code>T</code> and reads data from the network
	 */
	T read(ByteBuf buf);
	
}
