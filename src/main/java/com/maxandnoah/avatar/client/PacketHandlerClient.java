package com.maxandnoah.avatar.client;

import com.maxandnoah.avatar.common.network.IPacketHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * Handles packets addressed to the client. Packets like
 * this have a C in their name.
 *
 */
public class PacketHandlerClient implements IPacketHandler {

	@Override
	public IMessage onPacketReceived(IMessage packet, MessageContext ctx) {
		
		
		return null;
	}

	@Override
	public Side getSide() {
		return Side.CLIENT;
	}

}
