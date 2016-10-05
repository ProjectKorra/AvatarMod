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
public class PacketSUseStatusControl extends AvatarPacket<PacketSUseStatusControl> {
	
	private StatusControl statusControl;
	
	public PacketSUseStatusControl() {}
	
	public PacketSUseStatusControl(StatusControl control) {
		this.statusControl = control;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		statusControl = StatusControl.lookup(buf.readInt());
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(statusControl.id());
	}
	
	@Override
	protected Side getRecievedSide() {
		return Side.SERVER;
	}
	
	@Override
	protected AvatarPacket.Handler<PacketSUseStatusControl> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
	public StatusControl getStatusControl() {
		return statusControl;
	}
	
}
