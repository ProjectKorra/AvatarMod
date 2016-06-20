package com.maxandnoah.avatar.common.network.packets;

import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketSCheatEarthbending implements IAvatarPacket<PacketSCheatEarthbending> {
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
	}

	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	@Override
	public IMessage onMessage(PacketSCheatEarthbending message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}

}
