package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.VectorI;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class PacketSUseStatusControl extends AvatarPacket<PacketSUseStatusControl> {
	
	private StatusControl statusControl;
	private VectorI lookPos;
	private EnumFacing lookSide;
	
	public PacketSUseStatusControl() {}
	
	public PacketSUseStatusControl(StatusControl control, Raytrace.Result raytrace) {
		this.statusControl = control;
		this.lookPos = raytrace.getPos();
		this.lookSide = raytrace.getSide();
		System.out.println("SC: " + control.id());
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int id = buf.readInt();
		statusControl = StatusControl.lookup(id);
		if (statusControl == null) {
			AvatarLog
					.warn("Player trying to crash the server?? While sending UseStatusControl packet, sent invalid id "
							+ id);
			return; // TODO Cancel packet processing
		}
		
		VectorI readPos = VectorI.fromBytes(buf);
		int readSide = buf.readInt();
		
		if (readSide == -1) {
			lookPos = null;
			lookSide = null;
		} else {
			lookPos = readPos;
			lookSide = EnumFacing.values()[readSide];
		}
		
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(statusControl.id());
		if (lookPos == null) {
			new VectorI(0, 0, 0).toBytes(buf);
			buf.writeInt(-1);
		} else {
			lookPos.toBytes(buf);
			buf.writeInt(lookSide.ordinal());
		}
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
	
	public VectorI getLookPos() {
		return lookPos;
	}
	
	public EnumFacing getLookSide() {
		return lookSide;
	}
	
}
