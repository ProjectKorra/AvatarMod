package com.maxandnoah.avatar.common.network.packets;

import java.util.UUID;

import com.maxandnoah.avatar.AvatarLog;
import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;

public class PacketSCheckBendingList implements IAvatarPacket<PacketSCheckBendingList> {
	
	private UUID playerID;
	
	/**
	 * Called using reflection by SimpleImpl. Fields are initialized in fromBytes.
	 */
	public PacketSCheckBendingList() {}
	
	public PacketSCheckBendingList(UUID playerID) {
		this.playerID = playerID;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		playerID = GoreCoreByteBufUtil.readUUID(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeUUID(buf, playerID);
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	public UUID getPlayerID() {
		return playerID;
	}

	@Override
	public IMessage onMessage(PacketSCheckBendingList message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}

}
