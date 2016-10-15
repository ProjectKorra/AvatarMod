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
	
	private StatusControl control;
	
	public PacketCStatusControl() {}
	
	public PacketCStatusControl(StatusControl control) {
		this.control = control;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		control = StatusControl.lookup(buf.readInt());
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(control.id());
	}
	
	@Override
	protected Side getRecievedSide() {
		return Side.CLIENT;
	}
	
	@Override
	protected AvatarPacket.Handler<PacketCStatusControl> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
	public StatusControl getStatusControl() {
		return control;
	}
	
}
