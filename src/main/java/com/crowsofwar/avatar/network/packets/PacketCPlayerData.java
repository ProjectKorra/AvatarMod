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
import com.crowsofwar.avatar.util.data.AvatarPlayerData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.DataCategory;
import com.crowsofwar.gorecore.GoreCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static com.crowsofwar.gorecore.util.GoreCoreByteBufUtil.readUUID;
import static com.crowsofwar.gorecore.util.GoreCoreByteBufUtil.writeUUID;

/**
 * @author CrowsOfWar
 */
public class PacketCPlayerData extends AvatarPacket<PacketCPlayerData> {

	/**
	 * Used server-side to find what to write<br />
	 * Used client-side to write to
	 */
	private BendingData data;
	private UUID playerId;

	// We need a SortedSet since the order of writing/reading the data is
	// determined by the order of the DataCategories
	// If there was a regular set(no ordering guarantees), the order could have
	// changed, which would make the data get read/written in the wrong order
	// which would probably cause bugs/crashing
	private SortedSet<DataCategory> changed;

	public PacketCPlayerData() {
	}

	public PacketCPlayerData(BendingData data, UUID player, SortedSet<DataCategory> changed) {
		this.data = data;
		this.playerId = player;
		this.changed = changed;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		playerId = readUUID(buf);
		EntityPlayerSP clientP = FMLClientHandler.instance().getClientPlayerEntity();
		if (GoreCore.proxy.getClientSidePlayer() != null || clientP != null) {
			AvatarPlayerData playerData = AvatarPlayerData.fetcher().fetch(GoreCore.proxy.getClientSidePlayer() == null ?
			clientP.world : GoreCore.proxy.getClientSidePlayer().world, playerId);
			if (playerData != null) {
				final BendingData data = playerData.getData();

				if (data != null) {

					// Find what changed
					changed = new TreeSet<>();
					int size = buf.readInt();
					for (int i = 0; i < size; i++) {
						changed.add(DataCategory.values()[buf.readInt()]);
					}

					// Read what changed

					// For the scheduled task, can't continue to use this old bytebuf - it will be
					// deallocated and trying to read it will throw an error
					// So, just make a copy of the bytebuf, which will NOT be deallocated
					ByteBuf copyBuf = buf.copy();
					AvatarMod.proxy.getClientThreadListener().addScheduledTask(() -> {
						for (DataCategory category : changed) {
							category.read(copyBuf, data);
						}
					});

				}
			}
		} else {
			AvatarLog.warn(WarningType.WEIRD_PACKET, "Server sent a packet about data for player " + playerId
					+ " but data couldn't be found for them");

			// Get rid of bytes from ByteBuf which were unread
			// They aren't used, but this is necessary since read/write need to use same number
			// of bytes (or there will be big problems)

			BendingData trashData = new BendingData(dataCategory -> {
			}, () -> {
			});

			changed = new TreeSet<>();
			int size = buf.readInt();
			for (int i = 0; i < size; i++) {
				changed.add(DataCategory.values()[buf.readInt()]);
			}
			for (DataCategory category : changed) {
				category.read(buf, trashData);
			}

		}

	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		writeUUID(buf, playerId);

		// Tell client what has changed
		buf.writeInt(changed.size());
		for (DataCategory category : changed) {
			buf.writeInt(category.ordinal());
		}

		// The "real" payload - player data
		for (DataCategory category : changed) {
			category.write(buf, data);
		}

	}

	@Override
	protected Side getReceivedSide() {
		return Side.CLIENT;
	}

	@Override
	protected com.crowsofwar.avatar.network.packets.AvatarPacket.Handler<PacketCPlayerData> getPacketHandler() {

		// Do nothing!
		// In the avatarFromBytes method, already saved the data into the
		// player-data (when DataCategory.read is called)

		return (msg, ctx) -> null;
	}

	public UUID getPlayerId() {
		return playerId;
	}

}
