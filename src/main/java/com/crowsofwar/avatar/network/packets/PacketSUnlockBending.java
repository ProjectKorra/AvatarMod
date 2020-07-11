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

import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class PacketSUnlockBending extends AvatarPacket<PacketSUnlockBending> {

	private byte type;

	public PacketSUnlockBending() {
	}

	public PacketSUnlockBending(UUID type) {
		this.type = BendingStyles.getNetworkId(type);
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		type = buf.readByte();
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeByte(type);
	}

	@Override
	protected Side getReceivedSide() {
		return Side.SERVER;
	}

	@Override
	protected com.crowsofwar.avatar.network.packets.AvatarPacket.Handler<PacketSUnlockBending> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	public UUID getUnlockType() {
		return BendingStyles.get(type).getId();
	}

}
