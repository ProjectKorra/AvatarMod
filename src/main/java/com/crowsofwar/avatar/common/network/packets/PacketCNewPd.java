package com.crowsofwar.avatar.common.network.packets;

import static com.crowsofwar.gorecore.util.GoreCoreByteBufUtil.readUUID;

import java.util.UUID;

import com.crowsofwar.avatar.common.network.Networker;
import com.crowsofwar.avatar.common.network.PacketModularData;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class PacketCNewPd extends PacketModularData<PacketCNewPd> {
	
	private UUID playerId;
	
	public PacketCNewPd() {}
	
	public PacketCNewPd(Networker networker, UUID player) {
		super(networker);
		this.playerId = player;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		playerId = readUUID(buf);
		super.fromBytes(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeUUID(buf, playerId);
		super.toBytes(buf);
	}
	
	@Override
	protected Side getRecievedSide() {
		return Side.CLIENT;
	}
	
	@Override
	protected com.crowsofwar.avatar.common.network.packets.AvatarPacket.Handler<PacketCNewPd> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
	public UUID getPlayerId() {
		return playerId;
	}
	
}
