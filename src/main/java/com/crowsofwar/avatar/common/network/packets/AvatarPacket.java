package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.AvatarMod;

import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class AvatarPacket<MSG extends IMessage> implements IMessage, IMessageHandler<MSG, IMessage> {
	
	public AvatarPacket() {}
	
	@Override
	public final IMessage onMessage(MSG message, MessageContext ctx) {
		
		Handler<MSG> handler = getPacketHandler();
		
		IThreadListener mainThread = getRecievedSide().isServer()
				? ctx.getServerHandler().playerEntity.getServerWorld()
				: AvatarMod.proxy.getClientThreadListener();
		
		mainThread.addScheduledTask(() -> {
			handler.onMessageRecieved(message, ctx);
		});
		
		return handler.getResponse(message);
		
	}
	
	/**
	 * Returns the side that this packet is meant to be received on.
	 */
	protected abstract Side getRecievedSide();
	
	/**
	 * Get a packet handler which contains the logic for when this packet is received.
	 */
	protected abstract Handler<MSG> getPacketHandler();
	
	/**
	 * An interface to handle the packet being received.
	 * <p>
	 * Method must be implemented: {@link #onMessageRecieved(MSG, MessageContext)}.
	 * <p>
	 * Optional method {@link #getResponse(MSG)} allows you to send a follow-up apcket.
	 * 
	 * @param <MSG>
	 *            The type of the message to receive
	 * 
	 * @author CrowsOfWar
	 */
	@FunctionalInterface
	public interface Handler<MSG extends IMessage> {
		
		/**
		 * Called to handle the packet being received.
		 * 
		 * @param message
		 *            The actual instance of the received packet. You use this instance to retrieve
		 *            the necessary data.
		 * @param ctx
		 *            The context of the message. Can be used to obtain necessary objects such as a
		 *            player entity.
		 */
		void onMessageRecieved(MSG message, MessageContext ctx);
		
		/**
		 * Get the response packet to this message. Please note, this is called BEFORE
		 * {@link #onMessageRecieved(IMessage, MessageContext)}.
		 * <p>
		 * <strong>EXTREMELY IMPORTANT:</strong> Make sure that you don't access/modify vanilla
		 * Minecraft fields. This method is called on a different thread and you might end up
		 * causing concurrency issues.
		 * 
		 * @param message
		 *            The actual message
		 * 
		 * @return Follow-up packet to the given packet, or null for none
		 */
		default IMessage getResponse(MSG message) {
			return null;
		}
		
	}
	
}
