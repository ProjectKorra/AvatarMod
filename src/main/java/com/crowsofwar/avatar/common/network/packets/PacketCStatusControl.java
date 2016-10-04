package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.network.PacketRedirector;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Packet from server -> client to add a status control to the crosshair.
 * 
 * @author CrowsOfWar
 */
public class PacketCStatusControl extends AvatarPacket<PacketCStatusControl> {
	
	private int controlId;
	
	public PacketCStatusControl() {}
	
	public PacketCStatusControl(StatusControl control) {
		this.controlId = control.id();
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		controlId = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(controlId);
	}
	
	@Override
	protected Side getRecievedSide() {
		return Side.CLIENT;
	}
	
	@Override
	protected AvatarPacket.Handler<PacketCStatusControl> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
}
