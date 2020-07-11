/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.network.packets;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.AvatarMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author CrowsOfWar
 */
public abstract class AvatarPacket<MSG extends IMessage> implements IMessage, IMessageHandler<MSG, IMessage> {

	public AvatarPacket() {
	}

	@Override
	public final IMessage onMessage(MSG message, MessageContext ctx) {

		Handler<MSG> handler = getPacketHandler();

		IThreadListener mainThread = getReceivedSide().isServer()
				? ctx.getServerHandler().player.getServerWorld()
				: AvatarMod.proxy.getClientThreadListener();

		mainThread.addScheduledTask(() -> {
			IMessage followup = handler.onMessageRecieved(message, ctx);
			if (followup != null) {
				if (ctx.side.isClient()) {
					AvatarMod.network.sendToServer(followup);
				} else {
					AvatarMod.network.sendTo(followup, ctx.getServerHandler().player);
				}
			}
		});

		return null;

	}

	@Override
	public final void fromBytes(ByteBuf buf) {
		try {
			avatarFromBytes(buf);
		} catch (RuntimeException ex) {
			AvatarLog.warn(WarningType.BAD_CLIENT_PACKET,
					"Error processing packet " + getClass().getSimpleName(), ex);
		}
	}

	@Override
	public final void toBytes(ByteBuf buf) {
		try {
			avatarToBytes(buf);
		} catch (RuntimeException ex) {
			AvatarLog.warn(WarningType.BAD_CLIENT_PACKET,
					"Error processing packet " + getClass().getSimpleName(), ex);
		}
	}

	protected abstract void avatarFromBytes(ByteBuf buf);

	protected abstract void avatarToBytes(ByteBuf buf);

	/**
	 * Returns the side that this packet is meant to be received on.
	 */
	protected abstract Side getReceivedSide();

	/**
	 * Get a packet handler which contains the logic for when this packet is
	 * received.
	 */
	protected abstract Handler<MSG> getPacketHandler();

	/**
	 * An interface to handle the packet being received.
	 * <p>
	 * Method must be implemented:
	 * {@link #onMessageRecieved(MSG, MessageContext)}.
	 *
	 * @param <MSG> The type of the message to receive
	 * @author CrowsOfWar
	 */
	@FunctionalInterface
	public interface Handler<MSG extends IMessage> {

		/**
		 * Called to handle the packet being received.
		 *
		 * @param message The actual instance of the received packet. You use this
		 *                instance to retrieve the necessary data.
		 * @param ctx     The context of the message. Can be used to obtain
		 *                necessary objects such as a player entity.
		 * @return The follow-up packet, null for none. Note: doesn't actually
		 * use default SimpleImpl follow ups.
		 */
		IMessage onMessageRecieved(MSG message, MessageContext ctx);

	}

}
