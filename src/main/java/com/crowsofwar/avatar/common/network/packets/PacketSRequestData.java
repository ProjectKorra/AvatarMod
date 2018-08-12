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

package com.crowsofwar.avatar.common.network.packets;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.*;
import com.crowsofwar.gorecore.util.AccountUUIDs.AccountId;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * Sent from client to server to request data about a player.
 */
public class PacketSRequestData extends AvatarPacket<PacketSRequestData> {
	private UUID asking;

	public PacketSRequestData() {
	}

	public PacketSRequestData(UUID asking) {
		this.asking = asking;
	}

	public PacketSRequestData(EntityPlayer player) {
		AccountId result = AccountUUIDs.getId(player.getName());
		asking = result.getUUID();
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		asking = GoreCoreByteBufUtil.readUUID(buf);
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		GoreCoreByteBufUtil.writeUUID(buf, asking);
	}

	public UUID getAskedPlayer() {
		return asking;
	}

	public static class Handler extends AvatarPacketHandler<PacketSRequestData, IMessage> {

		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketSRequestData message, MessageContext ctx) {
			UUID id = message.getAskedPlayer();
			EntityPlayer player = AccountUUIDs.findEntityFromUUID(ctx.getServerHandler().player.world, id);
			if (player == null) {
				AvatarLog.warnHacking(ctx.getServerHandler().player.getName(),
									  "Sent request data for a player with account '" + id + "', but that player is not in the world.");
				return null;
			}
			BendingData.get(player).saveAll();
			return null;
		}
	}
}
