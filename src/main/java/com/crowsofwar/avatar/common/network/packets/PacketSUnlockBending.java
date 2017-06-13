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
 * 
 * 
 * @author CrowsOfWar
 */
public class PacketSUnlockBending extends AvatarPacket<PacketSUnlockBending> {
	
	private int type;
	
	public PacketSUnlockBending() {}
	
	public PacketSUnlockBending(int type) {
		this.type = type;
	}
	
	@Override
	public void avatarFromBytes(ByteBuf buf) {
		type = buf.readInt();
	}
	
	@Override
	public void avatarToBytes(ByteBuf buf) {
		buf.writeInt(type);
	}
	
	@Override
	protected Side getRecievedSide() {
		return Side.SERVER;
	}
	
	@Override
	protected com.crowsofwar.avatar.common.network.packets.AvatarPacket.Handler<PacketSUnlockBending> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
	public int getUnlockType() {
		return type;
	}
	
}
