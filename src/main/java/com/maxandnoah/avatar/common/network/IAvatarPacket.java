package com.maxandnoah.avatar.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * 
 * @param <REQ> Should be this class.
 */
public interface IAvatarPacket<REQ extends IAvatarPacket> extends IMessage, IMessageHandler<REQ, IMessage> {
	
	/**
	 * Get the side which the packet will be processed upon.
	 * @return
	 */
	public Side getRecievedSide();
	
}
