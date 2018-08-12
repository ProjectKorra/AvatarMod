package com.crowsofwar.avatar.common.network.packets;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.util.AvatarUtils;

import java.util.Objects;

public abstract class AvatarPacketHandler<MSG extends AvatarPacket, REPLY extends IMessage> implements IMessageHandler<MSG, REPLY> {
	@Override
	public final REPLY onMessage(MSG message, MessageContext ctx) {
		try {
			return Objects.requireNonNull(AvatarUtils.callFromMainThread(() -> avatarOnMessage(message, ctx))).get();
		} catch (Exception ex) {
			AvatarLog.error("Packet " + getClass().getSimpleName() + " generated an error upon handling. Report this to the authors!", ex);
			return null;
		}
	}

	/**
	 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
	 *
	 * @param message The packet that is received
	 * @param ctx     The context to that packet
	 * @return An optional packet to reply with, or null
	 */
	abstract REPLY avatarOnMessage(MSG message, MessageContext ctx);
}
