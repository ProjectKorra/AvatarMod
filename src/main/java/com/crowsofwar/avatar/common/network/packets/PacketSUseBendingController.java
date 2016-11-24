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

public class PacketSUseBendingController extends AvatarPacket<PacketSUseBendingController> {
	
	private int id;
	
	public PacketSUseBendingController() {}
	
	public PacketSUseBendingController(int id) {
		this.id = id;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
	}
	
	@Override
	public Side getRecievedSide() {
		return Side.SERVER;
	}
	
	public int getBendingControllerId() {
		return id;
	}
	
	@Override
	protected AvatarPacket.Handler<PacketSUseBendingController> getPacketHandler() {
		return PacketRedirector::redirectMessage;
	}
	
}
