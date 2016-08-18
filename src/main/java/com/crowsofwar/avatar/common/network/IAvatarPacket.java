package com.crowsofwar.avatar.common.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * @param <REQ>
 *            Should be this class.
 */
public interface IAvatarPacket<REQ extends IAvatarPacket> extends IMessage, IMessageHandler<REQ, IMessage> {
	
	/**
	 * Get the side which the packet will be processed upon.
	 * 
	 * @return
	 */
	public Side getRecievedSide();
	
}
