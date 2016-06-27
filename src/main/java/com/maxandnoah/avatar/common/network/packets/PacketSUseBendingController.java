package com.maxandnoah.avatar.common.network.packets;

import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketSUseBendingController implements IAvatarPacket<PacketSUseBendingController> {

	private int id;
	
	public PacketSUseBendingController() {}
	
	public PacketSUseBendingController(int id) {
		this.id = id;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
	}

	@Override
	public IMessage onMessage(PacketSUseBendingController message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}

	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	public int getBendingControllerId() {
		return id;
	}
	
}
