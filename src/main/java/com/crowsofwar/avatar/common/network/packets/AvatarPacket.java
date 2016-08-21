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
			IMessage followup = handler.onMessageRecieved(message, ctx);
			if (followup != null) {
				if (ctx.side.isClient()) {
					AvatarMod.network.sendToServer(followup);
				} else {
					AvatarMod.network.sendTo(followup, ctx.getServerHandler().playerEntity);
				}
			}
		});
		
		return null;
		
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
		 * 
		 * @return The follow-up packet, null for none. Note: doesn't actually use default
		 *         SimpleImpl follow ups.
		 */
		IMessage onMessageRecieved(MSG message, MessageContext ctx);
		
	}
	
}
