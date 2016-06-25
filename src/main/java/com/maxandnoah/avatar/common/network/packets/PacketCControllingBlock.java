package com.maxandnoah.avatar.common.network.packets;

import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketCControllingBlock implements IAvatarPacket<PacketCControllingBlock> {

	private int controlID;
	
	public PacketCControllingBlock() {}
	
	public PacketCControllingBlock(int controlID) {
		this.controlID = controlID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		controlID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(controlID);
	}

	@Override
	public IMessage onMessage(PacketCControllingBlock message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}

	@Override
	public Side getRecievedSide() {
		return Side.CLIENT;
	}
	
	public int getFloatingBlockID() {
		return controlID;
	}
	
}
