package com.maxandnoah.avatar.common.network.packets;

import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

/**
 * Packet to the server to toggle if bending is active. This will
 * be changed in the future !! When it is received on the server,
 * Earthbending is toggled on/off. Again, this is a very temporary
 * solution.
 *
 */
public class PacketSToggleBending implements IAvatarPacket<PacketSToggleBending> {
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	@Override
	public IMessage onMessage(PacketSToggleBending message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
}
