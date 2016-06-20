package com.maxandnoah.avatar.common.network;

import com.maxandnoah.avatar.AvatarMod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * Redirect a packet to the correct sided packet handler.
 * Contains a single method, {@link #redirectMessage(IMessage, MessageContext)}.
 * Not to be instantiated.
 *
 */
public class PacketRedirector {
	
	/**
	 * Only use static methods. Not to be instantiated.
	 */
	private PacketRedirector() {}
	
	public static IMessage redirectMessage(IMessage message, MessageContext ctx) {
		System.out.println("Received a new packet!");
		IPacketHandler packetHandler;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT) {
			packetHandler = AvatarMod.proxy.getClientPacketHandler();
		} else {
			packetHandler = PacketHandlerServer.instance;
		}
		System.out.println("Sending to: " + packetHandler);
		return packetHandler.onPacketReceived(message, ctx);
	}
	
}
