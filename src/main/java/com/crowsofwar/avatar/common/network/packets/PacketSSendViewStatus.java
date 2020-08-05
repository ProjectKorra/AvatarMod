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

import com.crowsofwar.avatar.common.network.PacketRedirector;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Packet to inform the server about the client's view mode
 * 
 * @author Aang23
 */
public class PacketSSendViewStatus extends AvatarPacket<PacketSSendViewStatus> {

	private int mode;

	public PacketSSendViewStatus() {
	}

	public PacketSSendViewStatus(int mode) {
		this.mode = mode;
	}

	@Override
	public void avatarFromBytes(ByteBuf buf) {
		mode = buf.readInt();
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeInt(mode);
	}

	@Override
	public Side getReceivedSide() {
		return Side.SERVER;
	}

	@Override
	protected AvatarPacket.Handler<PacketSSendViewStatus> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	public int getMode() {
		return mode;
	}
	
}
