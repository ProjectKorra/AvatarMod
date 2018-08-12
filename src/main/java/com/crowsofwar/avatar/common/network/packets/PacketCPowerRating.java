package com.crowsofwar.avatar.common.network.packets;

import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;

import java.util.*;

public class PacketCPowerRating extends AvatarPacket<PacketCPowerRating> {
	private Map<UUID, Double> powerRatings;

	public PacketCPowerRating() {
	}

	public PacketCPowerRating(Map<UUID, Double> powerRatings) {
		this.powerRatings = powerRatings;
	}

	@Override
	protected void avatarFromBytes(ByteBuf buf) {
		int size = buf.readInt();
		powerRatings = new HashMap<>();
		for (int i = 0; i < size; i++) {
			UUID bendingType = GoreCoreByteBufUtil.readUUID(buf);
			double powerRating = buf.readDouble();
			powerRatings.put(bendingType, powerRating);
		}
	}

	@Override
	protected void avatarToBytes(ByteBuf buf) {
		buf.writeInt(powerRatings.size());
		Set<Map.Entry<UUID, Double>> entries = powerRatings.entrySet();
		for (Map.Entry<UUID, Double> entry : entries) {
			GoreCoreByteBufUtil.writeUUID(buf, entry.getKey());
			buf.writeDouble(entry.getValue());
		}
	}

	public Map<UUID, Double> getPowerRatings() {
		return powerRatings;
	}

	public static class Handler extends AvatarPacketHandler<PacketCPowerRating, IMessage> {
		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketCPowerRating message, MessageContext ctx) {
			BendingData data = BendingData.get(Minecraft.getMinecraft().player);
			for (Map.Entry<UUID, Double> entry : message.getPowerRatings().entrySet()) {
				Objects.requireNonNull(data.getPowerRatingManager(entry.getKey())).setCachedRatingValue(entry.getValue());
			}
			return null;
		}
	}
}
