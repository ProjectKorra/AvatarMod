package com.maxandnoah.avatar.common.network.packets;

import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;

/**
 * Packet which tells the server that the client pressed a key.
 * The control is given to the player's active bending controller.
 * This is only used for controls that will be 
 *
 */
public class PacketSKeypress implements IAvatarPacket<PacketSKeypress> {
	
	private String control;
	
	public PacketSKeypress() {}
	
	public PacketSKeypress(String control) {
		this.control = control;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		control = GoreCoreByteBufUtil.readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeString(buf, control);
	}

	@Override
	public IMessage onMessage(PacketSKeypress message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	public String getControlPressed() {
		return control;
	}
	
}
