package com.crowsofwar.avatar.common.network;

import com.crowsofwar.avatar.AvatarMod;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Redirect a packet to the correct sided packet handler. Contains a single method,
 * {@link #redirectMessage(IMessage, MessageContext)}. Not to be instantiated.
 *
 */
public class PacketRedirector {
	
	/**
	 * Only use static methods. Not to be instantiated.
	 */
	private PacketRedirector() {}
	
	public static IMessage redirectMessage(IMessage message, MessageContext ctx) {
		IPacketHandler packetHandler;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT) {
			packetHandler = AvatarMod.proxy.getClientPacketHandler();
		} else {
			packetHandler = PacketHandlerServer.instance;
		}
		return packetHandler.onPacketReceived(message, ctx);
	}
	
}
