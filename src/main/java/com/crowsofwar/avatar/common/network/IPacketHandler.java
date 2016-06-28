package com.crowsofwar.avatar.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * Sided packet handler. The side is determined by which thread
 * it is running on. Therefore, for clients, there is both a
 * client packet handler and a server packet handler (due to
 * the presence of the integrated server).
 *
 */
public interface IPacketHandler {
	
	public IMessage onPacketReceived(IMessage packet, MessageContext ctx);
	
	public Side getSide();
	
}
