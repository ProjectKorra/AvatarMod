package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

	@Override
	protected Side getReceivedSide() {
		return Side.CLIENT;
	}

	@Override
	protected Handler<PacketCPowerRating> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	public Map<UUID, Double> getPowerRatings() {
		return powerRatings;
	}
}
