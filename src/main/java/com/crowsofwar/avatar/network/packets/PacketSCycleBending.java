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

import com.crowsofwar.avatar.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author CrowsOfWar
 */
public class PacketSCycleBending extends AvatarPacket<PacketSCycleBending> {

	private boolean right;

	public PacketSCycleBending() {
	}

	public PacketSCycleBending(boolean right) {
		this.right = right;
	}

	@Override
	protected void avatarFromBytes(ByteBuf buf) {
		right = buf.readBoolean();
	}

	@Override
	protected void avatarToBytes(ByteBuf buf) {
		buf.writeBoolean(right);
	}

	@Override
	protected Side getReceivedSide() {
		return Side.SERVER;
	}

	@Override
	protected com.crowsofwar.avatar.network.packets.AvatarPacket.Handler<PacketSCycleBending> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}

	public boolean cycleRight() {
		return right;
	}

}
