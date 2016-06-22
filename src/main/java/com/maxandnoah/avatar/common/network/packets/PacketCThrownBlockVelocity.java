package com.maxandnoah.avatar.common.network.packets;

import com.maxandnoah.avatar.common.entity.EntityFloatingBlock;
import com.maxandnoah.avatar.common.network.IAvatarPacket;
import com.maxandnoah.avatar.common.network.PacketRedirector;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.Vec3;

/**
 * Sent from server to client to notify the velocity of an
 * EntityFloatingBlock.
 *
 */
public class PacketCThrownBlockVelocity implements IAvatarPacket<PacketCThrownBlockVelocity> {
	
	private int id;
	private double x, y, z;
	
	public PacketCThrownBlockVelocity() {}
	
	public PacketCThrownBlockVelocity(EntityFloatingBlock floating) {
		id = floating.getID();
		Vec3 velocity = floating.getVelocity();
		x = velocity.xCoord;
		y = velocity.yCoord;
		z = velocity.zCoord;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	@Override
	public IMessage onMessage(PacketCThrownBlockVelocity message, MessageContext ctx) {
		return PacketRedirector.redirectMessage(message, ctx);
	}

	@Override
	public Side getRecievedSide() {
		return Side.CLIENT;
	}
	
	public Vec3 getVelocity() {
		return Vec3.createVectorHelper(x, y, z);
	}
	
	public int getBlockID() {
		return id;
	}
	
}
