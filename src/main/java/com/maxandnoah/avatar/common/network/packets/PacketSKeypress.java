package com.maxandnoah.avatar.common.network.packets;

import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;
import com.maxandnoah.avatar.common.util.BlockPos;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;

/**
 * Packet which tells the server that the client pressed a key.
 * The control is given to the player's active bending controller.
 *
 */
public class PacketSKeypress implements IAvatarPacket<PacketSKeypress> {
	
	private String control;
	private BlockPos target;
	
	public PacketSKeypress() {}
	
	public PacketSKeypress(String control, BlockPos target) {
		this.control = control;
		this.target = target;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		control = GoreCoreByteBufUtil.readString(buf);
		target = buf.readBoolean() ? BlockPos.fromBytes(buf) : null;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeString(buf, control);
		buf.writeBoolean(target != null);
		if (target != null) {
			target.toBytes(buf);
		}
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
	
	public BlockPos getTargetPos() {
		return target;
	}
	
}
