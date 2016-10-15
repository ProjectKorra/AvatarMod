package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.network.PacketRedirector;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class PacketCRemoveStatusControl extends AvatarPacket<PacketCRemoveStatusControl> {
	
	private StatusControl control;
	
	public PacketCRemoveStatusControl() {}
	
	public PacketCRemoveStatusControl(StatusControl control) {
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
	protected com.crowsofwar.avatar.common.network.packets.AvatarPacket.Handler<PacketCRemoveStatusControl> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
	public StatusControl getStatusControl() {
		return control;
	}
	
}
