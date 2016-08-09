package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.common.network.IAvatarPacket;
import com.crowsofwar.avatar.common.network.PacketRedirector;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

/**
 * Notify the client to attach control point entities to a certain arc.
 * 
 * @author CrowsOfWar
 */
public class PacketCControlPoints implements IAvatarPacket<PacketCControlPoints> {
	
	/**
	 * The Id of the arc which the control points are attached to.
	 */
	private int arcId;
	/**
	 * The Ids of all control points which are attached to the arc.
	 */
	private int[] controlPointIds;
	
	public PacketCControlPoints() {}
	
	public PacketCControlPoints(int arcId, int[] controlPointIds) {
		this.arcId = arcId;
		this.controlPointIds = controlPointIds;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		arcId = buf.readInt();
		controlPointIds = new int[buf.readInt()];
		for (int i = 0; i < controlPointIds.length; i++) {
			controlPointIds[i] = buf.readInt();
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(arcId);
		buf.writeInt(controlPointIds.length);
		for (int id : controlPointIds) {
			buf.writeInt(id);
		}
	}
	
	@Override
	public IMessage onMessage(PacketCControlPoints message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.CLIENT;
	}
	
	/**
	 * Get the Id of the arc which control points will attach to.
	 */
	public int getArcId() {
		return arcId;
	}
	
	/**
	 * Get the Ids of the control points which will be attached to the arc.
	 */
	public int[] getControlPointIds() {
		return controlPointIds;
	}
	
}
