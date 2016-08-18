package com.crowsofwar.avatar.common.network.packets;

import java.util.UUID;

import com.crowsofwar.avatar.common.network.IAvatarPacket;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs;
import com.crowsofwar.gorecore.util.GoreCorePlayerUUIDs.GetUUIDResult;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Sent from client to server to request data about a player.
 *
 */
public class PacketSRequestData implements IAvatarPacket<PacketSRequestData> {
	
	private UUID asking;
	
	public PacketSRequestData() {}
	
	public PacketSRequestData(UUID asking) {
		this.asking = asking;
	}
	
	public PacketSRequestData(EntityPlayer player) {
		GetUUIDResult result = GoreCorePlayerUUIDs.getUUID(player.getCommandSenderName());
		if (result.isResultSuccessful()) {
			this.asking = result.getUUID();
		} else {
			System.err.println("Couldn't get UUID for player " + player.getCommandSenderName() + " to send Request");
			result.logError();
		}
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		asking = GoreCoreByteBufUtil.readUUID(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeUUID(buf, asking);
	}
	
	@Override
	public IMessage onMessage(PacketSRequestData message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	public UUID getAskedPlayer() {
		return asking;
	}
	
}
