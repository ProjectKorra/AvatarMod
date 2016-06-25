package com.maxandnoah.avatar.client;

import com.maxandnoah.avatar.AvatarLog;
import com.maxandnoah.avatar.common.entity.EntityFloatingBlock;
import com.maxandnoah.avatar.common.network.IPacketHandler;
import com.maxandnoah.avatar.common.network.packets.PacketCControllingBlock;
import com.maxandnoah.avatar.common.network.packets.PacketCThrownBlockVelocity;
import com.maxandnoah.avatar.common.util.VectorUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Handles packets addressed to the client. Packets like
 * this have a C in their name.
 *
 */
public class PacketHandlerClient implements IPacketHandler {

	@Override
	public IMessage onPacketReceived(IMessage packet, MessageContext ctx) {
		if (packet instanceof PacketCThrownBlockVelocity)
			return handlePacketThrownBlockVelocity((PacketCThrownBlockVelocity) packet, ctx);
		
		if (packet instanceof PacketCControllingBlock)
			return handlePacketControllingBlock((PacketCControllingBlock) packet, ctx);
		
		AvatarLog.warn("Client recieved unknown packet from server:" + packet);
		
		return null;
	}

	@Override
	public Side getSide() {
		return Side.CLIENT;
	}

	private IMessage handlePacketThrownBlockVelocity(PacketCThrownBlockVelocity packet, MessageContext ctx) {
		World world = Minecraft.getMinecraft().theWorld;
		EntityFloatingBlock floating = EntityFloatingBlock.getFromID(world, packet.getBlockID());
		if (floating != null) {
			System.out.println("Set velocity on client");
			floating.setVelocity(packet.getVelocity());
		} else {
			AvatarLog.warn("PacketCThrownBlockVelocity- No block found with ID " + packet.getBlockID());
		}
		return null;
	}
	
	private IMessage handlePacketControllingBlock(PacketCControllingBlock packet, MessageContext ctx) {
		System.out.println("handlin pakit");
		World world = Minecraft.getMinecraft().theWorld;
		EntityFloatingBlock floating = EntityFloatingBlock.getFromID(world, packet.getFloatingBlockID());
		AvatarClientTick.instance.floating = floating;
		
		return null;
	}
	
}
