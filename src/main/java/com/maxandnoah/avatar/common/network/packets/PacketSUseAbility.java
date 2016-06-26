package com.maxandnoah.avatar.common.network.packets;

import com.maxandnoah.avatar.common.AvatarAbility;
import com.maxandnoah.avatar.common.controls.AvatarControl;
import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;
import com.maxandnoah.avatar.common.util.BlockPos;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

/**
 * Packet which tells the server that the client pressed a control.
 * The control is given to the player's active bending controller.
 * 
 * @see AvatarControl
 *
 */
public class PacketSUseAbility implements IAvatarPacket<PacketSUseAbility> {
	
	private AvatarAbility ability;
	private BlockPos target;
	
	public PacketSUseAbility() {}
	
	public PacketSUseAbility(AvatarAbility ability, BlockPos target) {
		this.ability = ability;
		this.target = target;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		ability = AvatarAbility.fromId(buf.readInt());
		target = buf.readBoolean() ? BlockPos.fromBytes(buf) : null;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(ability.getId());
		buf.writeBoolean(target != null);
		if (target != null) {
			target.toBytes(buf);
		}
	}

	@Override
	public IMessage onMessage(PacketSUseAbility message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	public AvatarAbility getAbility() {
		return ability;
	}
	
	public BlockPos getTargetPos() {
		return target;
	}
	
}
